package cn.codesensi.amour.service.file;

import cn.codesensi.amour.common.enums.StorageTypeEnum;
import cn.codesensi.amour.model.entity.SysFile;
import org.springframework.core.io.Resource;

/**
 * 文件存储抽象 —— 本地磁盘与对象存储（OSS）的统一访问口子。
 * <p>
 * 实现类均注册为 Spring Bean，{@code FileService} 按存储类型路由；
 * 新增存储实现时实现本接口即可，业务层与接口层零改动。
 *
 * @since 1.0
 */
public interface FileStorage {

    /**
     * 存储类型标识。
     *
     * @return 存储类型枚举
     */
    StorageTypeEnum getStorageType();

    /**
     * 存储文件内容。
     *
     * @param file  文件记录（id/bizType/extension 等已就绪，path 待写盘后回填）
     * @param bytes 文件字节内容
     * @return 相对存储 key（与 sys_file.path 同语义）
     */
    String upload(SysFile file, byte[] bytes);

    /**
     * 加载已存储的文件资源。
     * <p>
     * 本地实现返回磁盘文件资源；对象存储实现返回指向签名地址的资源。
     *
     * @param sysFile 文件记录
     * @return 文件资源
     */
    Resource load(SysFile sysFile);
}
