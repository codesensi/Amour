package cn.codesensi.amour.service.file;

import cn.codesensi.amour.common.enums.StorageTypeEnum;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.model.entity.SysFile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * 对象存储（OSS）实现
 * <p>
 * 仅作为存储方式的扩展口子：系统配置 file.storage 切换为 oss 后，
 * 上传与读取会走到此处并给出明确的未接入提示；正式接入时补充
 * endpoint/bucket/ak/sk 配置与 SDK 调用，业务层与接口层零改动。
 *
 * @since 1.0
 */
@Component
public class OssFileStorage implements FileStorage {

    /**
     * 存储类型标识。
     *
     * @return 对象存储
     */
    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.OSS;
    }

    /**
     * 存储文件内容到对象存储（预留,未接入）。
     *
     * @param file  文件记录
     * @param bytes 文件字节内容
     * @return 相对存储 key（object key）
     */
    @Override
    public String upload(SysFile file, byte[] bytes) {
        throw new SystemException("OSS 对象存储尚未接入,请先将系统配置 file.storage 切换为 local");
    }

    /**
     * 加载对象存储文件资源
     *
     * @param sysFile 文件记录
     * @return 文件资源
     */
    @Override
    public Resource load(SysFile sysFile) {
        throw new SystemException("OSS 对象存储尚未接入,无法读取文件：" + sysFile.getId());
    }

    /**
     * 删除对象存储文件（预留,未接入）。
     *
     * @param key 相对存储 key（object key）
     */
    @Override
    public void delete(String key) {
        throw new SystemException("OSS 对象存储尚未接入,无法删除文件：" + key);
    }
}
