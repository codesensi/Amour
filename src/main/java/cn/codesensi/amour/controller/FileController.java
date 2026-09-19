package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.properties.AppFileProperties;
import cn.codesensi.amour.service.FileService;
import cn.codesensi.amour.service.file.FileViewResult;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 文件 控制层。
 * <p>
 * 上传接口需登录；预览接口（/file/view/**）在 WebMvcConfig 中免登录放行，
 * 供无凭证的 img 标签等场景渲染；下载接口需登录。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final AppFileProperties appFileProperties;


    /**
     * 预览文件（内联）。
     * <p>
     * 免登录接口，按文件自身记录的 storage_type 分发读取；
     * Content-Type 由扩展名强推导，不采信记录中的历史声明值，杜绝伪装类型内联渲染；
     * 文件路径与内容一一对应，设置长期强缓存，二次访问直接命中浏览器缓存。
     *
     * @param id 文件ID
     * @return 文件流
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable("id") Long id) {
        FileViewResult result = fileService.load(id);
        // Content-Type 由扩展名查 Spring 内置 mime.types 强推导，不采信库中历史声明值（防伪装 text/html 的存储型 XSS）;
        // 与上传侧 FileServiceImpl#resolveContentType 同口径，未识别的扩展名一律以二进制流回显（浏览器不内联渲染）
        String extension = StrUtil.blankToDefault(result.sysFile().getExtension(), "").toLowerCase();
        MediaType mediaType = MediaTypeFactory.getMediaType("file." + extension)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(appFileProperties.getViewCacheDays(), TimeUnit.DAYS).cachePublic())
                .body(result.resource());
    }

}
