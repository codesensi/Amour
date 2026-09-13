package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.RegexConst;
import cn.codesensi.amour.common.enums.*;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.properties.AppFileProperties;
import cn.codesensi.amour.mapper.SysFileMapper;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.converter.FileConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.dto.FilePageDTO;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.response.FilePageResponse;
import cn.codesensi.amour.model.response.FileUploadResponse;
import cn.codesensi.amour.service.FileService;
import cn.codesensi.amour.service.SysConfigService;
import cn.codesensi.amour.service.file.FileStorage;
import cn.codesensi.amour.service.file.FileViewResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysFileTableDef.SYS_FILE;
import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;

/**
 * 文件服务实现。
 * <p>
 * 上传流程：业务类型校验 → 扩展名/大小校验 → 预生成主键 → 流式写盘 → 二次读取计算 MD5 → 单条 SQL 落库，
 * 任一环节失败整体回滚并清理已写盘文件。存储实现按 sys_config 的 {@code file.storage} 运行时路由
 * （实时读取支持系统配置页热更新），读取则按文件自身记录的 storage_type 分发，
 * 切换存储只影响新上传，历史文件不受影响。
 *
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
    private static final Pattern VIEW_URL_PATTERN = Pattern.compile(RegexConst.FILE_VIEW_URL);

    private final SysFileMapper sysFileMapper;
    private final SysUserMapper sysUserMapper;
    private final AppFileProperties props;
    private final SysConfigService sysConfigService;
    private final FileConverter fileConverter;
    private final Map<StorageTypeEnum, FileStorage> storageMap;

    /**
     * 构造时将全部存储实现按类型索引，运行时按配置路由。
     *
     * @param sysFileMapper    文件记录 Mapper
     * @param sysUserMapper    用户 Mapper（分页结果回填上传人用户名）
     * @param props            文件存储配置
     * @param sysConfigService 系统配置服务（读取 file.storage）
     * @param fileConverter    文件转换器（实体 → 行响应对象）
     * @param storages         全部存储实现（本地/对象存储等）
     */
    public FileServiceImpl(SysFileMapper sysFileMapper,
                           SysUserMapper sysUserMapper,
                           AppFileProperties props,
                           SysConfigService sysConfigService,
                           FileConverter fileConverter,
                           List<FileStorage> storages) {
        this.sysFileMapper = sysFileMapper;
        this.sysUserMapper = sysUserMapper;
        this.props = props;
        this.sysConfigService = sysConfigService;
        this.fileConverter = fileConverter;
        this.storageMap = storages.stream()
                .collect(Collectors.toUnmodifiableMap(FileStorage::getStorageType, Function.identity()));
    }

    /**
     * 上传文件。
     * <p>
     * 按业务类型完成扩展名与大小校验后，预生成主键并流式写盘，再对上传源二次读取计算 MD5，
     * 最后以单条 SQL 落库（含存储 key 与 MD5），任一环节失败整体回滚并清理已写盘文件；
     * 返回的分发地址形如 {@code /file/view/{id}}。
     *
     * @param bizType 业务类型编码（avatar/photo/markdown）
     * @param file    上传的文件
     * @return 文件ID、访问地址与原始文件名
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileUploadResponse upload(String bizType, MultipartFile file) {
        // 1. 业务类型校验
        FileBizTypeEnum bizTypeEnum = BaseEnum.fromCode(FileBizTypeEnum.class, bizType);
        if (bizTypeEnum == null) {
            throw new BusinessException("不支持的文件业务类型：" + bizType);
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 2. 清洗客户端文件名:去路径部分 + 移除换行符,防响应头 CRLF 注入;
        //    随后做扩展名/大小校验(规则由 FileBizTypeEnum 承载)
        String originalName = StrUtil.removeAllLineBreaks(
                FileNameUtil.getName(StrUtil.blankToDefault(file.getOriginalFilename(), "file")));
        String extension = FileNameUtil.extName(originalName).toLowerCase();
        if (StrUtil.isBlank(extension) || !bizTypeEnum.getExtensions().contains(extension)) {
            throw new BusinessException("不支持的文件格式,仅支持：" + String.join("/", bizTypeEnum.getExtensions()));
        }
        long maxBytes = bizTypeEnum.getMaxMb() * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小超过限制,最大 " + bizTypeEnum.getMaxMb() + "MB");
        }

        // 3. 解析存储方式并路由实现:实时读取 sys_config 的 file.storage(系统配置页修改即时生效),
        //    缺失时回退 yml 兜底值;无法识别的取值直接报错,避免静默落到错误存储
        String storageCode = sysConfigService.listByKeys(List.of(ConfigKeyEnum.FILE_STORAGE.getCode()))
                .stream().findFirst().map(ConfigDTO::getConfigValue)
                .orElse(props.getStorage());
        StorageTypeEnum storageType = BaseEnum.fromCode(StorageTypeEnum.class, storageCode);
        if (storageType == null) {
            throw new SystemException("不支持的文件存储方式：file.storage=" + storageCode);
        }
        FileStorage storage = requireStorage(storageType);

        // 4. 预生成主键并组装记录:存储 key 由主键参与拼装,先行生成可使入库合并为单条 SQL
        SysFile sysFile = new SysFile()
                .setId(IdUtil.getSnowflakeNextId())
                .setOriginalName(originalName)
                .setSize(file.getSize())
                .setStorageType(storage.getStorageType().getCode())
                .setExtension(extension)
                .setContentType(StrUtil.blankToDefault(file.getContentType(), "application/octet-stream"))
                .setBizType(bizTypeEnum.getCode());

        // 5. 流式写盘:不将文件整包读入内存,避免并发上传大文件时的内存尖峰
        String key;
        try (InputStream in = file.getInputStream()) {
            key = storage.upload(sysFile, in);
        } catch (IOException e) {
            throw new SystemException("读取上传文件失败");
        }

        // 6. 注册回滚清理:注册须先于 MD5 计算与入库,写盘之后任一环节失败回滚时同样清理已写盘的物理文件,避免残留孤儿文件
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    // 提交成功才保留物理文件；回滚或提交阶段失败均清理，避免残留孤儿文件
                    if (status != TransactionSynchronization.STATUS_COMMITTED) {
                        log.warn("上传事务未成功提交(status={})，清理已写盘的物理文件：key={}", status, key);
                        storage.delete(key);
                    }
                }
            });
        }

        // 7. 对上传源二次读取计算 MD5(Hutool 一次性消费流,返回小写十六进制);
        //    MultipartFile 底层为磁盘临时文件,可重复打开流,与存储实现解耦(本地/OSS 均适用)
        String md5;
        try (InputStream in = file.getInputStream()) {
            md5 = DigestUtil.md5Hex(in);
        } catch (IOException e) {
            throw new SystemException("读取上传文件失败");
        }
        sysFile.setMd5(md5);
        // 回填存储 key:预览/下载均按该 key 定位物理文件,缺失会导致 NPE
        sysFile.setPath(key);

        // 8. 单条 SQL 入库(主键/MD5/存储 key 均已就绪;主键已有值时框架自动跳过再生成)
        sysFileMapper.insert(sysFile, true);

        FileUploadResponse response = new FileUploadResponse()
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
        SysFile sysFile = sysFileMapper.selectOneById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        // 防御:存储 key 缺失的历史记录直接判不可用,避免物理定位时空指针
        if (StrUtil.isBlank(sysFile.getPath())) {
            throw new BusinessException("文件不存在或已被清理");
        }
        // 按文件自身记录的 storage_type 分发(与当前配置无关,切换存储不影响历史文件)
        StorageTypeEnum storageType = BaseEnum.fromCode(StorageTypeEnum.class, sysFile.getStorageType());
        if (storageType == null) {
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
     * 文件名模糊、业务类型/存储类型精确、上传人用户名模糊
     * （子查询先按用户名解析用户ID集合）、上传时间范围（起止均含边界），
     * 条件缺省时自动忽略；按 ID 倒序（最新在前）；
     * 上传人用户名按本页出现的 creator 批量回填，避免逐行查询。
     *
     * @param pageDTO 分页查询参数
     * @return 文件分页结果
     */
    @Override
    public Page<FilePageResponse> page(FilePageDTO pageDTO) {
        boolean recycled = DelFlagEnum.DELETED.getCode().equals(pageDTO.getDelFlag());
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SYS_FILE.DEL_FLAG.eq(recycled
                        ? DelFlagEnum.DELETED.getCode()
                        : DelFlagEnum.NOT_DELETED.getCode()))
                .and(SYS_FILE.ORIGINAL_NAME.like(pageDTO.getOriginalName(), StrUtil::isNotBlank))
                .and(SYS_FILE.BIZ_TYPE.eq(pageDTO.getBizType(), StrUtil::isNotBlank))
                .and(SYS_FILE.STORAGE_TYPE.eq(pageDTO.getStorageType(), StrUtil::isNotBlank))
                .and(SYS_FILE.CREATE_TIME.ge(parseTime(pageDTO.getBeginTime(), false), Objects::nonNull))
                .and(SYS_FILE.CREATE_TIME.le(parseTime(pageDTO.getEndTime(), true), Objects::nonNull))
                .orderBy(SYS_FILE.ID, false);
        if (StrUtil.isNotBlank(pageDTO.getCreatorName())) {
            // 上传人按用户名模糊匹配:子查询解析用户ID集合,避免联表分页
            QueryWrapper creatorQuery = QueryWrapper.create()
                    .select(SYS_USER.ID)
                    .from(SYS_USER)
                    .where(SYS_USER.USERNAME.like(pageDTO.getCreatorName()));
            wrapper.and(SYS_FILE.CREATOR.in(creatorQuery));
        }
        Page<SysFile> page = sysFileMapper.paginate(
                Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()), wrapper);

        // 批量回填上传人用户名:仅对本页出现的 creator 查询一次
        List<Long> creatorIds = page.getRecords().stream()
                .map(SysFile::getCreator)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> nameMap = creatorIds.isEmpty()
                ? Map.of()
                : sysUserMapper.selectListByIds(creatorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername, (a, b) -> a));
        Page<FilePageResponse> result = fileConverter.toPageResponse(page);
        result.getRecords().forEach(row -> {
            // creator 可能为空(未登录来源的历史记录),不可变 Map 拒绝 null key 查询,须先行判空
            if (row.getCreator() != null) {
                row.setCreatorName(nameMap.get(row.getCreator()));
            }
        });
        return result;
    }

    /**
     * 删除文件到回收站。
     * <p>
     * 仅逻辑删除记录，物理文件保留，业务展示不受影响
     * （预览按 id 加载，不校验删除标识）；物理文件在回收站“彻底删除”时统一清理，
     * 该口径与 {@code bindBizFiles} 的替换语义保持一致。
     *
     * @param id 文件ID
     */
    @Override
    public void delete(Long id) {
        SysFile sysFile = sysFileMapper.selectOneById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        if (DelFlagEnum.DELETED.getCode().equals(sysFile.getDelFlag())) {
            throw new BusinessException("文件已在回收站中,请勿重复删除");
        }
        UpdateChain.of(SysFile.class)
                .set(SYS_FILE.DEL_FLAG, DelFlagEnum.DELETED.getCode())
                .where(SYS_FILE.ID.eq(id))
                .and(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.NOT_DELETED.getCode()))
                .update();
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
        SysFile sysFile = sysFileMapper.selectOneById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        if (!DelFlagEnum.DELETED.getCode().equals(sysFile.getDelFlag())) {
            throw new BusinessException("文件不在回收站中,无需恢复");
        }
        UpdateChain.of(SysFile.class)
                .set(SYS_FILE.DEL_FLAG, DelFlagEnum.NOT_DELETED.getCode())
                .where(SYS_FILE.ID.eq(id))
                .and(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.DELETED.getCode()))
                .update();
    }

    /**
     * 彻底删除回收站文件。
     * <p>
     * 仅允许对已逻辑删除（回收站内）的记录执行；先兜底清理可能残留的物理文件
     * ——文件管理页删除的记录物理文件已清理，此处主要覆盖业务替换语义
     * （{@code bindBizFiles}）产生的孤儿文件——再物理删除记录。
     * 存储清理失败仅告警不抛出，不阻塞删行。
     *
     * @param id 文件ID
     */
    @Override
    public void physicalDelete(Long id) {
        SysFile sysFile = sysFileMapper.selectOneById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        if (!DelFlagEnum.DELETED.getCode().equals(sysFile.getDelFlag())) {
            throw new BusinessException("未删除的文件请先删除到回收站");
        }
        StorageTypeEnum storageType = BaseEnum.fromCode(StorageTypeEnum.class, sysFile.getStorageType());
        if (storageType == null) {
            throw new SystemException("不支持的存储类型：" + sysFile.getStorageType());
        }
        requireStorage(storageType).delete(sysFile.getPath());

        sysFileMapper.deleteById(id);
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
        if (bizType == null || bizId == null || CollUtil.isEmpty(urls)) {
            return;
        }

        // 1. 解析文件ID:仅识别本系统分发的 /file/view/{id} 形态,外链等静默跳过
        List<Long> fileIds = urls.stream()
                .filter(StrUtil::isNotBlank)
                .map(url -> {
                    var matcher = VIEW_URL_PATTERN.matcher(url);
                    return matcher.matches() ? Long.valueOf(matcher.group(1)) : null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(fileIds)) {
            return;
        }

        // 2. 绑定:回填业务关联ID(仅更新业务类型匹配且未删除的记录,幂等)
        UpdateChain.of(SysFile.class)
                .set(SYS_FILE.BIZ_ID, bizId)
                .where(SYS_FILE.ID.in(fileIds))
                .and(SYS_FILE.BIZ_TYPE.eq(bizType.getCode()))
                .and(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.NOT_DELETED.getCode()))
                .update();

        // 3. 替换:同业务下本次未引用的旧文件标记逻辑删除(如更换头像后的旧文件)
        UpdateChain.of(SysFile.class)
                .set(SYS_FILE.DEL_FLAG, DelFlagEnum.DELETED.getCode())
                .where(SYS_FILE.BIZ_TYPE.eq(bizType.getCode()))
                .and(SYS_FILE.BIZ_ID.eq(bizId))
                .and(SYS_FILE.ID.notIn(fileIds))
                .and(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.NOT_DELETED.getCode()))
                .update();
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
        if (storage == null) {
            throw new SystemException("文件存储实现未注册：" + type.getCode());
        }
        return storage;
    }
}
