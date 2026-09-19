package cn.codesensi.amour.controller.system;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.FileConverter;
import cn.codesensi.amour.model.dto.FileInfoDTO;
import cn.codesensi.amour.model.dto.FilePageDTO;
import cn.codesensi.amour.model.dto.FileUploadResultDTO;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.model.request.FilePageRequest;
import cn.codesensi.amour.model.response.FilePageResponse;
import cn.codesensi.amour.model.response.FileUploadResponse;
import cn.codesensi.amour.service.FileService;
import cn.codesensi.amour.service.file.FileViewResult;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
@RequestMapping("/sys/file")
public class SysFileController {

    private final FileService fileService;
    private final FileConverter fileConverter;

    /**
     * 上传文件。
     * <p>
     * 登录即可上传（个人中心与用户管理表单均使用），业务类型决定扩展名与大小校验规则。
     * 类级未标注 {@code ApiResponseBody}（预览/下载需原样流式输出），仅上传方法单独标注。
     *
     * @param bizType 业务类型（infra/avatar/photo/markdown，详见 FileBizTypeEnum）
     * @param file    上传的文件
     * @return 文件ID、访问地址与原始文件名
     */
    @ApiResponseBody
    @Log(module = "文件管理", operation = "上传文件", type = LogTypeEnum.UPLOAD)
    @PostMapping("/upload/{bizType}")
    public FileUploadResponse upload(@PathVariable("bizType") String bizType,
                                     @RequestParam("file") MultipartFile file) {
        FileUploadResultDTO result = fileService.upload(bizType, file);
        return fileConverter.toResponse(result);
    }

    /**
     * 分页查询文件记录。
     * <p>
     * 管理端文件管理页列表数据源；条件缺省时自动忽略，按 ID 倒序（最新在前）。
     *
     * @param request 分页查询参数
     * @return 文件分页结果
     */
    @ApiResponseBody
    @SaCheckPermission("system:file:page")
    @GetMapping("/page")
    public Page<FilePageResponse> page(@Valid FilePageRequest request) {
        FilePageDTO filePageDTO = fileConverter.toPageDTO(request);
        Page<FileInfoDTO> dtoPage = fileService.page(filePageDTO);
        return fileConverter.toResponsePage(dtoPage);
    }

    /**
     * 删除文件到回收站。
     * <p>
     * 登录即可调用，服务端按归属校验（上传人本人或具备 system:file:delete 权限），
     * 兼作业务侧清除图片（头像/站点Logo等）的删除入口；
     * 仅逻辑删除记录，物理文件保留，业务展示不受影响；
     * 可在回收站恢复或彻底删除（物理文件在彻底删除时统一清理）。
     *
     * @param id 文件ID
     */
    @ApiResponseBody
    @Log(module = "文件管理", operation = "删除文件", type = LogTypeEnum.DELETE, saveResult = false)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        fileService.delete(id);
    }

    /**
     * 恢复回收站文件。
     * <p>
     * 仅恢复文件记录本身（删除标识置回未删除），不自动回滚业务引用；
     * 如用户头像仍指向现文件，恢复旧头像不会改变页面展示。
     *
     * @param id 文件ID
     */
    @ApiResponseBody
    @Log(module = "文件管理", operation = "恢复文件", type = LogTypeEnum.UPDATE)
    @SaCheckPermission("system:file:delete")
    @PutMapping("/{id}/restore")
    public void restore(@PathVariable("id") Long id) {
        fileService.restore(id);
    }

    /**
     * 彻底删除回收站文件。
     * <p>
     * 仅允许对已逻辑删除（回收站内）的记录执行；兜底清理可能残留的物理文件
     * （如业务替换语义产生的孤儿文件）后物理删除记录，不可恢复。
     *
     * @param id 文件ID
     */
    @ApiResponseBody
    @Log(module = "文件管理", operation = "彻底删除文件", type = LogTypeEnum.DELETE, saveResult = false)
    @SaCheckPermission("system:file:delete")
    @DeleteMapping("/{id}/physical")
    public void physicalDelete(@PathVariable("id") Long id) {
        fileService.physicalDelete(id);
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
