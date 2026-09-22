package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.LoveListConverter;
import cn.codesensi.amour.model.dto.LoveListItemDTO;
import cn.codesensi.amour.model.request.LoveListChangeHiddenRequest;
import cn.codesensi.amour.model.request.LoveListInsertRequest;
import cn.codesensi.amour.model.request.LoveListPageRequest;
import cn.codesensi.amour.model.request.LoveListUpdateRequest;
import cn.codesensi.amour.model.response.LoveListPageResponse;
import cn.codesensi.amour.service.LoveListService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 恋爱清单管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/love-list")
public class AdminLoveListController {

    private final LoveListService loveListService;

    private final LoveListConverter loveListConverter;

    /**
     * 分页查询恋爱清单（管理端，完整字段，含隐藏项）。
     * <p>
     * 内容为模糊匹配，完成状态与显隐为精确匹配，
     * 条件缺省时自动忽略；排序为 sort 升序 → id 升序。
     *
     * @param pageRequest 分页查询参数
     * @return 清单项分页结果
     */
    @SaCheckPermission("admin:love-list:page")
    @GetMapping("/page")
    public Page<LoveListPageResponse> page(@Valid LoveListPageRequest pageRequest) {
        Page<LoveListItemDTO> itemPage = loveListService.pageAdmin(loveListConverter.toPageDTO(pageRequest));
        return loveListConverter.toPageResponse(itemPage);
    }

    /**
     * 新增清单项。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("admin:love-list:insert")
    @Log(module = "恋爱清单", operation = "新增清单项", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody LoveListInsertRequest insertRequest) {
        loveListService.insert(loveListConverter.toInsertDTO(insertRequest));
    }

    /**
     * 修改清单项（按 id 覆盖全部可编辑字段，完成状态与显隐并入表单维护）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("admin:love-list:update")
    @Log(module = "恋爱清单", operation = "修改清单项", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody LoveListUpdateRequest updateRequest) {
        loveListService.update(loveListConverter.toUpdateDTO(updateRequest));
    }

    /**
     * 修改清单项显隐。
     *
     * @param request 清单项显隐请求参数
     */
    @SaCheckPermission("admin:love-list:update")
    @Log(module = "恋爱清单", operation = "修改清单项显隐", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-hidden")
    public void changeHidden(@Valid @RequestBody LoveListChangeHiddenRequest request) {
        loveListService.changeHidden(loveListConverter.toChangeHiddenDTO(request));
    }

    /**
     * 批量逻辑删除清单项。
     *
     * @param ids 清单项ID集合（雪花ID字符串化传输）
     */
    @SaCheckPermission("admin:love-list:delete")
    @Log(module = "恋爱清单", operation = "删除清单项", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        loveListService.delete(List.of(ids));
    }
}
