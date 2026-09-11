package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.model.response.FileUploadResponse;
import cn.codesensi.amour.service.FileService;
import cn.codesensi.amour.service.file.FileViewResult;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件。
     * <p>
     * 登录即可上传（个人中心与用户管理表单均使用），业务类型决定扩展名与大小校验规则。
     * 类级未标注 {@code ApiResponseBody}（预览/下载需原样流式输出），仅上传方法单独标注。
     *
     * @param bizType 业务类型（avatar/photo/markdown）
     * @param file    上传的文件
     * @return 文件ID、访问地址与原始文件名
     */
    @ApiResponseBody
    @Log(module = "文件管理", operation = "上传文件", type = LogTypeEnum.UPLOAD)
    @PostMapping("/upload/{bizType}")
    public FileUploadResponse upload(@PathVariable("bizType") String bizType,
                                     @RequestParam("file") MultipartFile file) {
        return fileService.upload(bizType, file);
    }

    /**
     * 预览文件（内联）。
     * <p>
     * 免登录接口,按文件自身记录的 storage_type 分发读取；
     * 文件路径与内容一一对应,设置长期强缓存,二次访问直接命中浏览器缓存。
     *
     * @param id 文件ID
     * @return 文件流
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable("id") Long id) {
        FileViewResult result = fileService.load(id);
        // Content-Type 缺失或非法时回退为二进制流
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(StrUtil.blankToDefault(
                    result.sysFile().getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE));
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .body(result.resource());
    }

    /**
     * 下载文件（附件）。
     * <p>
     * 以原始文件名触发浏览器另存为；响应头按 RFC 5987 编码文件名以支持中文。
     *
     * @param id 文件ID
     * @return 文件流
     */
    @Log(module = "文件管理", operation = "下载文件", type = LogTypeEnum.DOWNLOAD, saveResult = false)
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        FileViewResult result = fileService.load(id);
        SysFile sysFile = result.sysFile();
        String fallbackName = StrUtil.blankToDefault(sysFile.getOriginalName(), "file-" + sysFile.getId());
        // RFC 5987 编码文件名以支持中文,axios 场景下空格会被编码为 +,需还原为 %20
        String encoded = URLEncoder.encode(fallbackName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(result.resource());
    }
}
