package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RegexConst;
import cn.codesensi.amour.common.enums.*;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.properties.AppFileProperties;
import cn.codesensi.amour.common.support.AuditUserFiller;
import cn.codesensi.amour.mapper.SysFileMapper;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.converter.FileConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.dto.FileDTO;
import cn.codesensi.amour.model.dto.FilePageDTO;
import cn.codesensi.amour.model.dto.FileUploadResultDTO;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.service.FileService;
import cn.codesensi.amour.service.SysConfigService;
import cn.codesensi.amour.service.file.FileStorage;
import cn.codesensi.amour.service.file.FileViewResult;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysFileTableDef.SYS_FILE;
import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;

/**
 * 文件服务实现。
 * <p>
 * 上传流程：业务类型校验 → 扩展名/大小/魔数校验 → 预生成主键 → 流式写盘 → 二次读取计算 MD5 → 单条 SQL 落库，
 * 任一环节失败整体回滚并清理已写盘文件。存储实现按 sys_config 的 {@code file.storage} 运行时路由
 * （实时读取支持系统配置页热更新），读取则按文件自身记录的 storage_type 分发，
 * 切换存储只影响新上传，历史文件不受影响。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements FileService {

    /**
     * 文件分发访问 URL 前缀（与 RbacConst.FILE_VIEW_PATH 对齐）
     */
    private static final String VIEW_URL_PREFIX = "/file/view/";

    /**
     * 文件分发 URL 解析器：仅识别本系统 /file/view/{id} 形态
     */
    private static final Pattern VIEW_URL_PATTERN = Pattern.compile(RegexConst.LITERALS_FILE_VIEW_DIGITS);

    /**
     * 魔数识别读入的文件头长度:覆盖枚举中最长的 WEBP 魔数判定(偏移 0~11),
     * 新增需要更长文件头的格式时按需调大
     */
    private static final int MAGIC_HEAD_BYTES = 12;

    private final SysFileMapper sysFileMapper;
    private final SysUserMapper sysUserMapper;
    private final AppFileProperties props;
    private final SysConfigService sysConfigService;
    private final FileConverter fileConverter;
    private final AuditUserFiller auditUserFiller;
    private final Map<StorageTypeEnum, FileStorage> storageMap;

    /**
     * 构造时将全部存储实现按类型索引，运行时按配置路由。
     *
     * @param sysFileMapper    文件记录 Mapper
     * @param sysUserMapper    用户 Mapper（上传人用户名模糊匹配时解析用户ID集合）
     * @param props            文件存储配置
     * @param sysConfigService 系统配置服务（读取 file.storage）
     * @param fileConverter    文件转换器（实体 → 行响应对象）
     * @param auditUserFiller  审计用户回填器（分页结果回填上传人用户名）
     * @param storages         全部存储实现（本地/对象存储等）
     */
    public FileServiceImpl(SysFileMapper sysFileMapper,
                           SysUserMapper sysUserMapper,
                           AppFileProperties props,
                           SysConfigService sysConfigService,
                           FileConverter fileConverter,
                           AuditUserFiller auditUserFiller,
                           List<FileStorage> storages) {
        this.sysFileMapper = sysFileMapper;
        this.sysUserMapper = sysUserMapper;
        this.props = props;
        this.sysConfigService = sysConfigService;
        this.fileConverter = fileConverter;
        this.auditUserFiller = auditUserFiller;
        this.storageMap = storages.stream()
                .collect(Collectors.toUnmodifiableMap(FileStorage::getStorageType, Function.identity()));
    }

    /**
     * 上传文件。
     * <p>
     * 按业务类型完成扩展名与大小校验后，预生成主键并流式写盘，再对上传源二次读取计算 MD5，
     * 最后以单条 SQL 落库（含存储 key 与 MD5）。写盘与 MD5 属慢速 IO，不包裹事务，
     * 避免长时间占用数据库连接；入库为单条 SQL 原子操作，失败时清理已写盘文件避免孤儿残留；
     * 上传内容经文件头魔数校验真实类型，防改后缀伪装；
     * 返回的分发地址形如 {@code /file/view/{id}}。
     *
     * @param bizType 业务类型编码（infra/avatar/photo/markdown，详见 FileBizTypeEnum）
     * @param file    上传的文件
     * @return 文件ID、访问地址与原始文件名
     */
    @Override
    public FileUploadResultDTO upload(String bizType, MultipartFile file) {
        // 1. 业务类型校验
        FileBizTypeEnum bizTypeEnum = BaseEnum.fromCode(FileBizTypeEnum.class, bizType);
        if (ObjUtil.isNull(bizTypeEnum)) {
            throw new BusinessException("不支持的文件业务类型：" + bizType);
        }
        if (ObjUtil.isNull(file) || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 2. 清洗客户端文件名:去路径部分 + 移除换行符，防响应头 CRLF 注入;
        //    随后做扩展名/大小校验（规则由 FileBizTypeEnum 承载）
        String originalName = StrUtil.removeAllLineBreaks(
                FileNameUtil.getName(StrUtil.blankToDefault(file.getOriginalFilename(), "file")));
        String extension = FileNameUtil.extName(originalName).toLowerCase();
        if (StrUtil.isBlank(extension) || !bizTypeEnum.accepts(extension)) {
            throw new BusinessException("不支持的文件格式,仅支持：" + bizTypeEnum.getFormats().stream()
                    .map(FileTypeEnum::getCode).collect(Collectors.joining(AppConst.SLASH)));
        }
        long maxBytes = bizTypeEnum.getMaxMb() * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小超过限制,最大 " + bizTypeEnum.getMaxMb() + "MB");
        }

        // 3. 魔数校验:读文件头比对真实类型,防改后缀伪装(扩展名仅是客户端声明)
        FileTypeEnum magic = verifyMagicNumber(extension, file);

        // 4. 解析存储方式并路由实现:实时读取 sys_config 的 file.storage（系统配置页修改即时生效），
        //    缺失时回退 yml 兜底值;无法识别的取值直接报错，避免静默落到错误存储
        String storageCode = sysConfigService.listByKeys(List.of(ConfigKeyEnum.FILE_STORAGE.getCode()))
                .stream().findFirst().map(ConfigDTO::getConfigValue)
                .orElse(props.getStorage());
        StorageTypeEnum storageType = BaseEnum.fromCode(StorageTypeEnum.class, storageCode);
        if (ObjUtil.isNull(storageType)) {
            throw new SystemException("不支持的文件存储方式：file.storage=" + storageCode);
        }
        FileStorage storage = requireStorage(storageType);

        // 5. 预生成主键并组装记录:存储 key 由主键参与拼装，先行生成可使入库合并为单条 SQL
        // Content-Type 取魔数枚举的权威 MIME（与魔数校验同源），不采信客户端声明值（防伪装类型的存储型 XSS）
        String contentType = magic.getMimeType();
        SysFile sysFile = new SysFile()
                .setId(IdUtil.getSnowflakeNextId())
                .setOriginalName(originalName)
                .setSize(file.getSize())
                .setStorageType(storage.getStorageType().getCode())
                .setExtension(extension)
                .setContentType(contentType)
                .setBizType(bizTypeEnum.getCode());

        // 6. 流式写盘:不将文件整包读入内存，避免并发上传大文件时的内存尖峰
        String key;
        try (InputStream in = file.getInputStream()) {
            key = storage.upload(sysFile, in);
        } catch (IOException e) {
            throw new SystemException("读取上传文件失败");
        }

        // 7. 对上传源二次读取计算 MD5（Hutool 一次性消费流，返回小写十六进制）;
        //    MultipartFile 底层为磁盘临时文件，可重复打开流，与存储实现解耦（本地/OSS 均适用）
        String md5;
        try (InputStream in = file.getInputStream()) {
            md5 = DigestUtil.md5Hex(in);
        } catch (IOException e) {
            throw new SystemException("读取上传文件失败");
        }
        sysFile.setMd5(md5);
        // 回填存储 key:预览/下载均按该 key 定位物理文件，缺失会导致 NPE
        sysFile.setPath(key);

        // 8. 入库:主键/MD5/存储 key 均已就绪（主键已有值时框架自动跳过再生成）。
        //    守护器保证入库失败时兜底清理已写盘的物理文件，避免残留孤儿文件
        try (var guard = new UploadedFileGuard(storage, key)) {
            sysFileMapper.insert(sysFile, true);
            guard.committed();
        }

        FileUploadResultDTO response = new FileUploadResultDTO()
                .setId(sysFile.getId())
                .setUrl(VIEW_URL_PREFIX + sysFile.getId())
                .setOriginalName(originalName);
        log.debug("文件上传完成：id={}, bizType={}, storage={}, path={}, size={}",
                sysFile.getId(), bizTypeEnum.getCode(), storage.getStorageType().getCode(), key, file.getSize());
        return response;
    }

    /**
     * 加载文件记录与存储资源。
     * <p>
     * 按文件自身记录的 storage_type 分发，与当前配置的存储方式无关，
     * 保证切换存储后历史文件仍可读取。
     *
     * @param id 文件ID
     * @return 文件记录与存储资源
     */
    @Override
    public FileViewResult load(Long id) {
        // 绕过全局逻辑删除:预览需覆盖回收站内的已删除文件，删除标识不作为过滤条件
        SysFile sysFile = LogicDeleteManager.execWithoutLogicDelete(() ->
                sysFileMapper.selectOneById(id));
        if (ObjUtil.isNull(sysFile)) {
            throw new BusinessException("文件不存在");
        }
        // 防御:存储 key 缺失的历史记录直接判不可用，避免物理定位时空指针
        if (StrUtil.isBlank(sysFile.getPath())) {
            throw new BusinessException("文件不存在或已被清理");
        }
        // 按文件自身记录的 storage_type 分发（与当前配置无关，切换存储不影响历史文件）
        StorageTypeEnum storageType = BaseEnum.fromCode(StorageTypeEnum.class, sysFile.getStorageType());
        if (ObjUtil.isNull(storageType)) {
            throw new SystemException("不支持的存储类型：" + sysFile.getStorageType());
        }
        FileStorage storage = requireStorage(storageType);
        Resource resource = storage.load(sysFile);
        if (!resource.exists()) {
            throw new BusinessException("文件不存在或已被清理");
        }
        return new FileViewResult(sysFile, resource);
    }

    /**
     * 分页查询文件记录。
     * <p>
     * 按删除标识区分数据域：0-未删除（文件列表，缺省），1-已删除（回收站）；
     * 查询显式绕过全局逻辑删除（否则自动追加的 del_flag=0 会与回收站条件冲突），
     * 数据域条件由本方法自行指定；
     * 文件名模糊、业务类型/存储类型精确、上传人用户名模糊
     * （先按用户名解析用户ID集合再 IN）、上传时间范围（起止均含边界），
     * 条件缺省时自动忽略；按 ID 倒序（最新在前）；
     * 上传人用户名按本页出现的 creator 批量回填，避免逐行查询。
     *
     * @param pageDTO 分页查询参数
     * @return 文件分页结果
     */
    @Override
    public Page<FileDTO> page(FilePageDTO pageDTO) {
        boolean recycled = DelFlagEnum.DELETED.getCode().equals(pageDTO.getDelFlag());
        // 上传人按用户名模糊匹配:先解析用户ID集合再 IN，避免联表分页（对齐用户模块的既有惯例）;
        // 与主查询同处绕过逻辑删除的作用域，已删除用户的文件仍可按用户名命中（与原 IN 子查询行为一致）
        List<Long> creatorIds;
        if (StrUtil.isNotBlank(pageDTO.getCreatorName())) {
            List<Long> resolved = LogicDeleteManager.execWithoutLogicDelete(() ->
                    QueryChain.of(sysUserMapper)
                            .select(SYS_USER.ID)
                            .where(SYS_USER.USERNAME.like(pageDTO.getCreatorName()))
                            .listAs(Long.class));
            if (CollUtil.isEmpty(resolved)) {
                // 上传人条件无匹配用户，直接返回空页，避免空 IN 查询
                return Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize(), 0);
            }
            creatorIds = resolved;
        } else {
            creatorIds = List.of();
        }
        // 绕过全局逻辑删除:回收站需按 del_flag=1 命中已删除记录，
        // 数据域由本方法显式的 DEL_FLAG 条件圈定（0-文件列表，1-回收站）
        Page<SysFile> page = LogicDeleteManager.execWithoutLogicDelete(() ->
                QueryChain.of(sysFileMapper)
                        .where(SYS_FILE.DEL_FLAG.eq(recycled
                                ? DelFlagEnum.DELETED.getCode()
                                : DelFlagEnum.NOT_DELETED.getCode()))
                        .and(SYS_FILE.ORIGINAL_NAME.like(pageDTO.getOriginalName(), StrUtil::isNotBlank))
                        .and(SYS_FILE.BIZ_TYPE.eq(pageDTO.getBizType(), StrUtil::isNotBlank))
                        .and(SYS_FILE.STORAGE_TYPE.eq(pageDTO.getStorageType(), StrUtil::isNotBlank))
                        .and(SYS_FILE.CREATE_TIME.ge(parseTime(pageDTO.getBeginTime(), false), Objects::nonNull))
                        .and(SYS_FILE.CREATE_TIME.le(parseTime(pageDTO.getEndTime(), true), Objects::nonNull))
                        .and(SYS_FILE.CREATOR.in(creatorIds, CollUtil::isNotEmpty))
                        .orderBy(SYS_FILE.ID, false)
                        .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize())));

        // 批量回填上传人用户名:统一走审计用户回填器（收集 creator/updater 主键单次查询）
        Page<FileDTO> result = fileConverter.toPageDTO(page);
        auditUserFiller.fill(result.getRecords());
        return result;
    }

    /**
     * 删除文件到回收站。
     * <p>
     * 仅逻辑删除记录，物理文件保留，业务展示不受影响
     * （预览按 id 加载，不校验删除标识）；物理文件在回收站“彻底删除”时统一清理，
     * 该口径与 {@code bindBizFiles} 的替换语义保持一致。
     * <p>
     * 权限口径：上传人本人或具备 system:file:delete 权限——
     * 本人口径兼容业务侧清除图片（头像/站点Logo等）复用本接口，
     * 权限口径保留文件管理页对任意文件的删除能力；
     * 对已在回收站内的文件幂等成功，便于业务侧未保存场景下的重复清除。
     *
     * @param id 文件ID
     */
    @Override
    public void delete(Long id) {
        // 绕过全局逻辑删除，才能识别已在回收站的记录并给出准确的重复删除提示
        SysFile sysFile = LogicDeleteManager.execWithoutLogicDelete(() ->
                sysFileMapper.selectOneById(id));
        if (ObjUtil.isNull(sysFile)) {
            throw new BusinessException("文件不存在");
        }
        // 归属校验:上传人本人（creator 可能为空的历史记录不匹配）或具备文件删除权限
        boolean ownFile = ObjUtil.equals(StpUtil.getLoginIdAsLong(), sysFile.getCreator());
        if (!ownFile && !StpUtil.hasPermission("system:file:delete")) {
            throw new BusinessException("仅可删除自己上传的文件");
        }
        // 已在回收站时幂等成功:业务侧"清除图片"未保存前可重复触发，
        // 且逻辑删除改写自动携带 del_flag=0 条件，重复执行本就无副作用
        if (DelFlagEnum.DELETED.getCode().equals(sysFile.getDelFlag())) {
            return;
        }
        sysFileMapper.deleteById(id);
    }

    /**
     * 恢复回收站文件。
     * <p>
     * 仅将逻辑删除记录的删除标识置回未删除，不自动回滚业务引用：
     * 如替换头像产生的旧文件，恢复后 biz_id 仍在，但页面展示跟随业务字段
     * （sys_user.avatar 等）不会变化；条件携带 del_flag 保证幂等。
     *
     * @param id 文件ID
     */
    @Override
    public void restore(Long id) {
        // 绕过全局逻辑删除:恢复的目标是已删除记录，查询与回置 del_flag 均需触达 del_flag=1 的行
        SysFile sysFile = LogicDeleteManager.execWithoutLogicDelete(() ->
                sysFileMapper.selectOneById(id));
        if (ObjUtil.isNull(sysFile)) {
            throw new BusinessException("文件不存在");
        }
        if (!DelFlagEnum.DELETED.getCode().equals(sysFile.getDelFlag())) {
            throw new BusinessException("文件不在回收站中,无需恢复");
        }
        LogicDeleteManager.execWithoutLogicDelete(() ->
                UpdateChain.of(SysFile.class)
                        .set(SYS_FILE.DEL_FLAG, DelFlagEnum.NOT_DELETED.getCode())
                        .where(SYS_FILE.ID.eq(id))
                        .and(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.DELETED.getCode()))
                        .update());
    }

    /**
     * 彻底删除回收站文件。
     * <p>
     * 仅允许对已逻辑删除（回收站内）的记录执行；先物理删除记录，成功后再兜底清理
     * 可能残留的物理文件——文件管理页删除的记录物理文件已清理，此处主要覆盖业务
     * 替换语义（{@code bindBizFiles}）产生的孤儿文件。
     * 存储清理失败仅告警不抛出，不阻塞删行。
     *
     * @param id 文件ID
     */
    @Override
    public void physicalDelete(Long id) {
        // 绕过全局逻辑删除:彻底删除的目标是已删除记录，且须执行真正的物理 DELETE
        //（默认 deleteById 会被框架改写为逻辑删除并对 del_flag=0 过滤，对已删除行是空操作）
        SysFile sysFile = LogicDeleteManager.execWithoutLogicDelete(() -> sysFileMapper.selectOneById(id));
        if (ObjUtil.isNull(sysFile)) {
            throw new BusinessException("文件不存在");
        }
        if (!DelFlagEnum.DELETED.getCode().equals(sysFile.getDelFlag())) {
            throw new BusinessException("未删除的文件请先删除到回收站");
        }
        StorageTypeEnum storageType = BaseEnum.fromCode(StorageTypeEnum.class, sysFile.getStorageType());
        if (ObjUtil.isNull(storageType)) {
            throw new SystemException("不支持的存储类型：" + sysFile.getStorageType());
        }
        // 先物理删除记录（保持绕过全局逻辑删除的真正 DELETE），成功后再清理物理文件，
        // 避免先删文件而删行失败时留下"记录仍在、文件已丢"的悬空记录
        LogicDeleteManager.execWithoutLogicDelete(() -> sysFileMapper.deleteById(id));

        requireStorage(storageType).delete(sysFile.getPath());
        log.debug("文件彻底删除完成：id={}, storageType={}, path={}", id, storageType.getCode(), sysFile.getPath());
    }

    /**
     * 将文件集合绑定到业务对象（采纳语义）。
     * <p>
     * 调用方传入业务对象当前引用的<b>全量</b> URL 集合：
     * 可解析出文件ID的（本系统文件）回填 {@code biz_id}；
     * 同业务类型与业务ID下本次未出现的旧文件标记逻辑删除（替换语义）；
     * 外链等无法解析的 URL 静默跳过，方法幂等可重入。
     *
     * @param bizType 业务类型
     * @param bizId   业务对象ID（新增场景由调用方在业务落库后传入）
     * @param urls    业务对象当前引用的全部文件 URL
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindBizFiles(FileBizTypeEnum bizType, Long bizId, Collection<String> urls) {
        if (ObjUtil.isNull(bizType) || ObjUtil.isNull(bizId) || CollUtil.isEmpty(urls)) {
            return;
        }

        // 1. 解析文件ID:仅识别本系统分发的 /file/view/{id} 形态，外链等静默跳过
        List<Long> fileIds = urls.stream()
                .filter(StrUtil::isNotBlank)
                .map(url -> {
                    Matcher matcher = VIEW_URL_PATTERN.matcher(url);
                    return matcher.matches() ? Long.valueOf(matcher.group(1)) : null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        // 跳过的 URL 记录 debug，便于排查业务引用未采纳的问题
        long skippedCount = urls.stream()
                .filter(StrUtil::isNotBlank)
                .filter(url -> !VIEW_URL_PATTERN.matcher(url).matches())
                .count();
        if (skippedCount > 0) {
            log.debug("存在无法解析为本系统文件的 URL，已跳过：bizType={}, bizId={}, skipped={}",
                    bizType.getCode(), bizId, skippedCount);
        }
        if (CollUtil.isEmpty(fileIds)) {
            return;
        }

        // 2. 绑定:回填业务关联ID（仅更新业务类型匹配且未删除的记录，幂等）
        UpdateChain.of(SysFile.class)
                .set(SYS_FILE.BIZ_ID, bizId)
                .where(SYS_FILE.ID.in(fileIds))
                .and(SYS_FILE.BIZ_TYPE.eq(bizType.getCode()))
                .and(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.NOT_DELETED.getCode()))
                .update();

        // 3. 替换:同业务下本次未引用的旧文件标记逻辑删除（如更换头像后的旧文件）;
        // deleteByQuery 走全局逻辑删除，自动携带 del_flag=0 条件保证幂等
        sysFileMapper.deleteByQuery(QueryChain.of(sysFileMapper)
                .where(SYS_FILE.BIZ_TYPE.eq(bizType.getCode()))
                .and(SYS_FILE.BIZ_ID.eq(bizId))
                .and(SYS_FILE.ID.notIn(fileIds)));
    }

    /**
     * 将业务对象当前绑定的文件全部解除引用（替换语义的空集形态）。
     * <p>
     * 将同业务类型与业务ID下的文件标记逻辑删除（进回收站），用于业务字段被置空的场景
     * （如清空头像）；物理文件保留，待回收站“彻底删除”时统一清理；
     * 走全局逻辑删除（自动携带 del_flag=0 条件），方法幂等可重入。
     *
     * @param bizType 业务类型
     * @param bizId   业务对象ID
     */
    @Override
    public void unbindBizFiles(FileBizTypeEnum bizType, Long bizId) {
        if (ObjUtil.isNull(bizType) || ObjUtil.isNull(bizId)) {
            return;
        }
        sysFileMapper.deleteByQuery(QueryChain.of(sysFileMapper)
                .where(SYS_FILE.BIZ_TYPE.eq(bizType.getCode()))
                .and(SYS_FILE.BIZ_ID.eq(bizId)));
    }

    /**
     * 解析上传时间范围边界。
     * <p>
     * 起边界取当日零点、止边界取当日末尾（含边界）；空值返回 null（查询条件自动忽略），
     * 解析失败抛出业务异常给出可读提示。
     *
     * @param date     日期文本（yyyy-MM-dd）
     * @param endOfDay true-取当日末尾（止边界），false-取当日零点（起边界）
     * @return 边界时间；空文本返回 null
     */
    private Date parseTime(String date, boolean endOfDay) {
        if (StrUtil.isBlank(date)) {
            return null;
        }
        try {
            Date parsed = DateUtil.parse(date);
            return endOfDay ? DateUtil.endOfDay(parsed) : DateUtil.beginOfDay(parsed);
        } catch (Exception e) {
            throw new BusinessException("时间格式不正确：" + date);
        }
    }

    /**
     * 按存储类型获取实现。
     *
     * @param type 存储类型
     * @return 存储实现
     */
    private FileStorage requireStorage(StorageTypeEnum type) {
        FileStorage storage = storageMap.get(type);
        if (ObjUtil.isNull(storage)) {
            throw new SystemException("文件存储实现未注册：" + type.getCode());
        }
        return storage;
    }

    /**
     * 校验上传内容的文件魔数与扩展名一致，防改后缀伪装（如把脚本/文本改名 .jpg 上传）。
     * <p>读上传流头部字节，由 {@link FileTypeEnum#fromMagic} 按魔数识别真实格式，
     * 与扩展名定位的期望类型比较；无法识别的伪造内容或空文件一律拒绝。
     * 读流不消费上传源，MultipartFile 可重复开流，后续写盘与 MD5 计算不受影响。
     *
     * @param extension 全小写扩展名
     * @param file      上传文件
     * @return 扩展名映射的期望格式枚举（校验通过后用于推导落库 Content-Type）
     */
    private FileTypeEnum verifyMagicNumber(String extension, MultipartFile file) {
        byte[] head;
        try (InputStream in = file.getInputStream()) {
            // 读头 12 字节:覆盖枚举中最长的 WEBP 魔数判定(偏移 0~11)
            head = in.readNBytes(MAGIC_HEAD_BYTES);
        } catch (IOException e) {
            throw new SystemException("读取上传文件失败");
        }
        FileTypeEnum fileTypeEnum = FileTypeEnum.fromExtension(extension);
        FileTypeEnum actual = head.length == 0 ? null : FileTypeEnum.fromMagic(head);
        if (fileTypeEnum == null || actual != fileTypeEnum) {
            throw new BusinessException("文件内容与扩展名不符，请上传真实的图片文件");
        }
        return fileTypeEnum;
    }

    /**
     * 已写盘文件的守护器 —— 以 try-with-resources 包裹「写盘之后、入库提交之前」的失败窗口，
     * 窗口内发生任何异常时兜底删除物理文件；入库成功后调用 {@link #committed()} 解除守护。
     * <p>清理自身的异常在 {@code close()} 内消化并留痕，不会掩盖入库失败的主异常。
     */
    private static class UploadedFileGuard implements AutoCloseable {

        private final FileStorage storage;

        private final String key;

        private boolean committed;

        private UploadedFileGuard(FileStorage storage, String key) {
            this.storage = storage;
            this.key = key;
        }

        /**
         * 入库成功，解除守护，物理文件得以保留。
         */
        private void committed() {
            this.committed = true;
        }

        @Override
        public void close() {
            if (committed) {
                return;
            }
            try {
                storage.delete(key);
            } catch (Exception e) {
                log.error("孤儿文件清理失败，需人工处理：key={}", key, e);
            }
        }
    }
}
