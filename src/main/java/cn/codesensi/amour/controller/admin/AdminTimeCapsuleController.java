package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.TimeCapsuleConverter;
import cn.codesensi.amour.model.dto.TimeCapsuleDTO;
import cn.codesensi.amour.model.dto.TimeCapsulePageDTO;
import cn.codesensi.amour.model.request.TimeCapsuleChangeHiddenRequest;
import cn.codesensi.amour.model.request.TimeCapsuleInsertRequest;
import cn.codesensi.amour.model.request.TimeCapsulePageRequest;
import cn.codesensi.amour.model.request.TimeCapsuleUpdateRequest;
import cn.codesensi.amour.model.response.TimeCapsulePageResponse;
import cn.codesensi.amour.service.TimeCapsuleService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 时间胶囊管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/time-capsule")
public class AdminTimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    private final TimeCapsuleConverter timeCapsuleConverter;

    /**
     * 分页查询时间胶囊（管理端，完整字段，含隐藏项与未解锁项）。
     * <p>
     * 标题为模糊匹配，显隐为精确匹配，条件缺省时自动忽略；
     * 排序为解锁时间升序 → id 升序。
     *
     * @param pageRequest 分页查询参数
     * @return 时间胶囊分页结果
     */
    @SaCheckPermission("admin:time-capsule:page")
    @GetMapping("/page")
    public Page<TimeCapsulePageResponse> page(@Valid TimeCapsulePageRequest pageRequest) {
        TimeCapsulePageDTO pageDTO = timeCapsuleConverter.toPageDTO(pageRequest);
        Page<TimeCapsuleDTO> itemPage = timeCapsuleService.pageAdmin(pageDTO);
        return timeCapsuleConverter.toPageResponse(itemPage);
    }

    /**
     * 新增时间胶囊。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("admin:time-capsule:insert")
    @Log(module = "时间胶囊", operation = "新增时间胶囊", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody TimeCapsuleInsertRequest insertRequest) {
        timeCapsuleService.insert(timeCapsuleConverter.toInsertDTO(insertRequest));
    }

    /**
     * 修改时间胶囊（按 id 覆盖全部可编辑字段，显隐除外）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("admin:time-capsule:update")
    @Log(module = "时间胶囊", operation = "修改时间胶囊", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody TimeCapsuleUpdateRequest updateRequest) {
        timeCapsuleService.update(timeCapsuleConverter.toUpdateDTO(updateRequest));
    }

    /**
     * 修改时间胶囊显隐。
     *
     * @param request 胶囊显隐请求参数
     */
    @SaCheckPermission("admin:time-capsule:update")
    @Log(module = "时间胶囊", operation = "修改时间胶囊显隐", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-hidden")
    public void changeHidden(@Valid @RequestBody TimeCapsuleChangeHiddenRequest request) {
        timeCapsuleService.changeHidden(timeCapsuleConverter.toChangeHiddenDTO(request));
    }

    /**
     * 批量逻辑删除时间胶囊。
     *
     * @param ids 胶囊ID集合（雪花ID字符串化传输）
     */
    @SaCheckPermission("admin:time-capsule:delete")
    @Log(module = "时间胶囊", operation = "删除时间胶囊", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        timeCapsuleService.delete(List.of(ids));
    }
}
