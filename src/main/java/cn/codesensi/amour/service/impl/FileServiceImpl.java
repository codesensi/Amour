package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.*;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.properties.AppFileProperties;
import cn.codesensi.amour.mapper.SysFileMapper;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.model.response.FileUploadResponse;
import cn.codesensi.amour.service.FileService;
import cn.codesensi.amour.service.SysConfigService;
import cn.codesensi.amour.service.file.FileStorage;
import cn.codesensi.amour.service.file.FileViewResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysFileTableDef.SYS_FILE;

/**
 * 文件服务实现。
 * <p>
 * 上传流程：业务类型校验 → 扩展名/大小校验 → 落库取得主键 → 以主键命名写盘 → 回填存储 key，
 * 任一环节失败整体回滚。存储实现按 sys_config 的 {@code file.storage} 运行时路由
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
    private static final Pattern VIEW_URL_PATTERN = Pattern.compile("^/file/view/(\\d+)$");

    private final SysFileMapper sysFileMapper;
    private final AppFileProperties props;
    private final SysConfigService sysConfigService;
    private final Map<StorageTypeEnum, FileStorage> storageMap;

    /**
     * 构造时将全部存储实现按类型索引，运行时按配置路由。
     *
     * @param sysFileMapper    文件记录 Mapper
     * @param props            文件存储配置
     * @param sysConfigService 系统配置服务（读取 file.storage）
     * @param storages         全部存储实现（本地/对象存储等）
     */
    public FileServiceImpl(SysFileMapper sysFileMapper,
                           AppFileProperties props,
                           SysConfigService sysConfigService,
                           List<FileStorage> storages) {
        this.sysFileMapper = sysFileMapper;
        this.props = props;
        this.sysConfigService = sysConfigService;
        this.storageMap = storages.stream()
                .collect(Collectors.toUnmodifiableMap(FileStorage::getStorageType, Function.identity()));
    }

    /**
     * 上传文件。
     * <p>
     * 按业务类型完成扩展名与大小校验后，先落库取得主键，再以主键命名写盘，
     * 任一环节失败整体回滚；返回的分发地址形如 {@code /file/view/{id}}。
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
        // 落库(主键由全局雪花生成器填充,path 待写盘后回填)
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new SystemException("读取上传文件失败");
        }
        SysFile sysFile = new SysFile()
                .setOriginalName(originalName)
                .setSize(file.getSize())
                .setMd5(DigestUtil.md5Hex(bytes))
                .setStorageType(storage.getStorageType().getCode())
                .setExtension(extension)
                .setContentType(StrUtil.blankToDefault(file.getContentType(), "application/octet-stream"))
                .setBizType(bizTypeEnum.getCode());
        sysFileMapper.insert(sysFile, true);

        // 4. 写盘(物理文件名使用主键ID)
        String key = storage.upload(sysFile, bytes);

        // 5. 回填存储 key
        SysFile pathUpdate = new SysFile().setId(sysFile.getId()).setPath(key);
        sysFileMapper.update(pathUpdate);

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
