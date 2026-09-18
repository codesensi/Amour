package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.FootprintConverter;
import cn.codesensi.amour.model.dto.FootprintItemDTO;
import cn.codesensi.amour.model.request.FootprintInsertRequest;
import cn.codesensi.amour.model.request.FootprintPageRequest;
import cn.codesensi.amour.model.request.FootprintUpdateRequest;
import cn.codesensi.amour.model.response.FootprintPageResponse;
import cn.codesensi.amour.service.FootprintService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 足迹地图管理相关接口 前端控制器
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/footprint")
public class SysFootprintController {

    private final FootprintService footprintService;
    private final FootprintConverter footprintConverter;

    /**
     * 分页查询足迹（管理端，完整字段）。
     * <p>
     * 城市为模糊匹配，到访日期为闭区间范围过滤，条件缺省时自动忽略；
     * 排序为到访日期升序 → id 升序（与门户时间轴口径一致）。
     *
     * @param pageRequest 分页查询参数
     * @return 足迹分页结果
     */
    @SaCheckPermission("system:footprint:page")
    @GetMapping("/page")
    public Page<FootprintPageResponse> page(@Valid FootprintPageRequest pageRequest) {
        Page<FootprintItemDTO> itemPage = footprintService.pageAdmin(footprintConverter.toPageDTO(pageRequest));
        return footprintConverter.toPageResponse(itemPage);
    }

    /**
     * 新增足迹（城市必填，经纬度/到访日期/照片/备注可空）。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("system:footprint:insert")
    @Log(module = "足迹地图", operation = "新增足迹", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody FootprintInsertRequest insertRequest) {
        footprintService.insert(footprintConverter.toInsertDTO(insertRequest));
    }

    /**
     * 修改足迹（按 id 覆盖全部可编辑字段）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("system:footprint:update")
    @Log(module = "足迹地图", operation = "修改足迹", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody FootprintUpdateRequest updateRequest) {
        footprintService.update(footprintConverter.toUpdateDTO(updateRequest));
    }

    /**
     * 批量逻辑删除足迹。
     *
     * @param ids 足迹ID集合(雪花ID字符串化传输)
     */
    @SaCheckPermission("system:footprint:delete")
    @Log(module = "足迹地图", operation = "删除足迹", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        footprintService.delete(List.of(ids));
    }
}
