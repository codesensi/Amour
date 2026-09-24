package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.MomentsConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.request.MomentsChangeStatusRequest;
import cn.codesensi.amour.model.request.MomentsInsertRequest;
import cn.codesensi.amour.model.request.MomentsPageRequest;
import cn.codesensi.amour.model.request.MomentsUpdateRequest;
import cn.codesensi.amour.model.response.MomentsHistoryResponse;
import cn.codesensi.amour.model.response.MomentsPageResponse;
import cn.codesensi.amour.service.MomentsService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点点滴滴管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/moments")
public class AdminMomentsController {

    private final MomentsService momentsService;

    private final MomentsConverter momentsConverter;

    /**
     * 分页查询点点滴滴文章（管理端，全量）。
     * <p>
     * 标题/分类/状态为条件匹配，条件缺省时自动忽略；
     * 排序为 sort 升序 → 记录日期降序 → id 降序。
     *
     * @param pageRequest 分页查询参数
     * @return 点点滴滴分页结果
     */
    @SaCheckPermission("admin:moments:page")
    @GetMapping("/page")
    public Page<MomentsPageResponse> page(@Valid MomentsPageRequest pageRequest) {
        MomentsPageDTO pageDTO = momentsConverter.toPageDTO(pageRequest);
        Page<MomentsDTO> itemPage = momentsService.pageAdmin(pageDTO);
        return momentsConverter.toPageResponse(itemPage);
    }

    /**
     * 历史分类/标签建议（去重后返回,供表单自由输入时自动补全）。
     *
     * @return 分类与标签去重列表
     */
    @SaCheckPermission("admin:moments:page")
    @GetMapping("/history")
    public MomentsHistoryResponse history() {
        MomentsHistoryDTO historyDTO = momentsService.history();
        return momentsConverter.toResponse(historyDTO);
    }

    /**
     * 新增点点滴滴文章（作者取当前登录人）。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("admin:moments:insert")
    @Log(module = "点点滴滴", operation = "新增点点滴滴文章", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody MomentsInsertRequest insertRequest) {
        MomentsInsertDTO insertDTO = momentsConverter.toInsertDTO(insertRequest);
        momentsService.insert(insertDTO);
    }

    /**
     * 修改点点滴滴文章（按 id 覆盖可编辑字段,作者归属不可变）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("admin:moments:update")
    @Log(module = "点点滴滴", operation = "修改点点滴滴文章", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody MomentsUpdateRequest updateRequest) {
        MomentsUpdateDTO updateDTO = momentsConverter.toUpdateDTO(updateRequest);
        momentsService.update(updateDTO);
    }

    /**
     * 修改文章显示状态。
     *
     * @param request 状态修改请求参数
     */
    @SaCheckPermission("admin:moments:update")
    @Log(module = "点点滴滴", operation = "修改文章状态", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-status")
    public void changeStatus(@Valid @RequestBody MomentsChangeStatusRequest request) {
        MomentsChangeStatusDTO changeStatusDTO = momentsConverter.toChangeStatusDTO(request);
        momentsService.changeStatus(changeStatusDTO);
    }

    /**
     * 批量逻辑删除点点滴滴文章。
     *
     * @param ids 文章ID集合（雪花ID字符串化传输）
     */
    @SaCheckPermission("admin:moments:delete")
    @Log(module = "点点滴滴", operation = "删除点点滴滴文章", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        momentsService.delete(List.of(ids));
    }
}
