package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.LovePhotoConverter;
import cn.codesensi.amour.model.dto.LovePhotoDTO;
import cn.codesensi.amour.model.request.LovePhotoChangeHiddenRequest;
import cn.codesensi.amour.model.request.LovePhotoInsertRequest;
import cn.codesensi.amour.model.request.LovePhotoPageRequest;
import cn.codesensi.amour.model.request.LovePhotoUpdateRequest;
import cn.codesensi.amour.model.response.LovePhotoPageResponse;
import cn.codesensi.amour.service.LovePhotoService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 恋爱画册管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/love-photo")
public class AdminLovePhotoController {

    private final LovePhotoService lovePhotoService;
    private final LovePhotoConverter lovePhotoConverter;

    /**
     * 分页查询恋爱画册照片（管理端，完整字段，含隐藏照片）。
     * <p>
     * 文案为模糊匹配，标签为逗号集合内精确匹配，显隐为精确匹配，
     * 条件缺省时自动忽略；排序为 sort 升序 → id 升序。
     *
     * @param pageRequest 分页查询参数
     * @return 照片分页结果
     */
    @SaCheckPermission("admin:love-photo:page")
    @GetMapping("/page")
    public Page<LovePhotoPageResponse> page(@Valid LovePhotoPageRequest pageRequest) {
        Page<LovePhotoDTO> itemPage = lovePhotoService.pageAdmin(lovePhotoConverter.toPageDTO(pageRequest));
        return lovePhotoConverter.toPageResponse(itemPage);
    }

    /**
     * 新增照片（标签集合规范化为逗号分隔存储）。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("admin:love-photo:insert")
    @Log(module = "恋爱画册", operation = "新增照片", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody LovePhotoInsertRequest insertRequest) {
        lovePhotoService.insert(lovePhotoConverter.toInsertDTO(insertRequest));
    }

    /**
     * 修改照片（按 id 覆盖全部可编辑字段；单独的显隐切换走 change-status）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("admin:love-photo:update")
    @Log(module = "恋爱画册", operation = "修改照片", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody LovePhotoUpdateRequest updateRequest) {
        lovePhotoService.update(lovePhotoConverter.toUpdateDTO(updateRequest));
    }

    /**
     * 修改照片显隐
     *
     * @param request 照片显隐请求参数
     */
    @SaCheckPermission("admin:love-photo:update")
    @Log(module = "恋爱画册", operation = "修改照片显隐", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-hidden")
    public void changeHidden(@Valid @RequestBody LovePhotoChangeHiddenRequest request) {
        lovePhotoService.changeHidden(lovePhotoConverter.toChangeHiddenDTO(request));
    }

    /**
     * 批量逻辑删除照片。
     *
     * @param ids 照片ID集合(雪花ID字符串化传输)
     */
    @SaCheckPermission("admin:love-photo:delete")
    @Log(module = "恋爱画册", operation = "删除照片", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        lovePhotoService.delete(List.of(ids));
    }
}
