package cn.codesensi.amour.service.file;

import cn.codesensi.amour.common.enums.StorageTypeEnum;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.properties.AppFileProperties;
import cn.codesensi.amour.model.entity.SysFile;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * 本地磁盘存储实现。
 * <p>
 * 文件按 {@code {bizType}/{yyyyMM}/{fileId}.{ext}} 落盘于配置的根目录下：
 * 一级目录按业务类型隔离，二级目录按月份分片防止单目录文件膨胀，
 * 物理文件名复用 sys_file 主键便于追溯。
 *
 * @since 1.0
 */
@Slf4j
@Component
public class LocalFileStorage implements FileStorage {

    /**
     * 本地存储根目录（绝对路径、规范化）
     */
    private final Path basePath;

    /**
     * 构造时一次性解析并规范化根目录，落盘与读取共用。
     *
     * @param props 文件存储配置
     */
    public LocalFileStorage(AppFileProperties props) {
        this.basePath = Path.of(props.getLocal().getBasePath()).toAbsolutePath().normalize();
    }

    /**
     * 存储文件内容到本地磁盘。
     *
     * @param file  文件记录（id/bizType/extension 等已就绪，path 待写盘后回填）
     * @param bytes 文件字节内容
     * @return 相对存储 key
     */
    @Override
    public String upload(SysFile file, byte[] bytes) {
        // 相对 key:{bizType}/{yyyyMM}/{fileId}.{ext},分月目录防单目录文件膨胀
        String month = DateUtil.format(new Date(), DatePattern.SIMPLE_MONTH_PATTERN);
        String key = file.getBizType() + "/" + month + "/" + file.getId() + "." + file.getExtension();
        Path target = resolve(key);
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException e) {
            log.error("本地文件写入失败：key={}", key, e);
            throw new SystemException("文件写入失败：" + key);
        }
        log.debug("本地文件写入成功：key={}, size={}", key, bytes.length);
        return key;
    }

    /**
     * 加载本地磁盘文件资源。
     *
     * @param sysFile 文件记录
     * @return 磁盘文件资源
     */
    @Override
    public Resource load(SysFile sysFile) {
        return new FileSystemResource(resolve(sysFile.getPath()));
    }

    /**
     * 存储类型标识。
     *
     * @return 本地存储
     */
    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.LOCAL;
    }

    /**
     * 解析相对 key 为绝对路径,并校验其未越出根目录（防路径穿越）。
     *
     * @param key 相对存储 key
     * @return 绝对路径
     */
    private Path resolve(String key) {
        Path target = basePath.resolve(key).normalize();
        if (!target.startsWith(basePath)) {
            throw new SystemException("非法的文件存储路径：" + key);
        }
        return target;
    }
}
