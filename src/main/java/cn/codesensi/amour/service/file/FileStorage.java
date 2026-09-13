package cn.codesensi.amour.service.file;

import cn.codesensi.amour.common.enums.StorageTypeEnum;
import cn.codesensi.amour.model.entity.SysFile;
import org.springframework.core.io.Resource;

import java.io.InputStream;

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
     * <p>
     * 实现方负责完整读取输入流并写入存储；流由调用方打开并关闭。
     *
     * @param file 文件记录（id/bizType/extension 等已就绪）
     * @param in   文件内容输入流（流式写入，避免整包读入内存）
     * @return 相对存储 key（与 sys_file.path 同语义）
     */
    String upload(SysFile file, InputStream in);

    /**
     * 加载已存储的文件资源。
     * <p>
     * 本地实现返回磁盘文件资源；对象存储实现返回指向签名地址的资源。
     *
     * @param sysFile 文件记录
     * @return 文件资源
     */
    Resource load(SysFile sysFile);

    /**
     * 删除已存储的物理文件。
     * <p>
     * 用于上传事务回滚等场景的物理文件清理；文件不存在时静默成功。
     *
     * @param key 相对存储 key（与 sys_file.path 同语义）
     */
    void delete(String key);
}
