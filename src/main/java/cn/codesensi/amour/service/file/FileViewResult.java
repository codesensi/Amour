package cn.codesensi.amour.service.file;

import cn.codesensi.amour.model.entity.SysFile;
import org.springframework.core.io.Resource;

/**
 * 文件读取结果 —— 文件记录与存储资源的组合，供控制器流式输出。
 *
 * @param sysFile  文件记录
 * @param resource 存储资源
 * @since 1.0
 */
public record FileViewResult(SysFile sysFile, Resource resource) {
}
