package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.AnniversaryConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.request.AnniversaryChangeHiddenRequest;
import cn.codesensi.amour.model.request.AnniversaryInsertRequest;
import cn.codesensi.amour.model.request.AnniversaryPageRequest;
import cn.codesensi.amour.model.request.AnniversaryUpdateRequest;
import cn.codesensi.amour.model.response.AnniversaryPageResponse;
import cn.codesensi.amour.service.AnniversaryService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 纪念日管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/anniversary")
public class AdminAnniversaryController {

    private final AnniversaryService anniversaryService;
    private final AnniversaryConverter anniversaryConverter;

    /**
     * 分页查询纪念日（管理端，全量）。
     * <p>
     * 名称模糊匹配，排序为纪念日日期升序 → id 升序。
     *
     * @param pageRequest 分页查询参数
     * @return 纪念日分页结果
     */
    @SaCheckPermission("admin:anniversary:page")
    @GetMapping("/page")
    public Page<AnniversaryPageResponse> page(@Valid AnniversaryPageRequest pageRequest) {
        AnniversaryPageDTO pageDTO = anniversaryConverter.toPageDTO(pageRequest);
        Page<AnniversaryDTO> pageAdmin = anniversaryService.pageAdmin(pageDTO);
        return anniversaryConverter.toPageResponse(pageAdmin);
    }

    /**
     * 新增纪念日。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("admin:anniversary:insert")
    @Log(module = "纪念日", operation = "新增纪念日", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody AnniversaryInsertRequest insertRequest) {
        AnniversaryInsertDTO insertDTO = anniversaryConverter.toInsertDTO(insertRequest);
        anniversaryService.insert(insertDTO);
    }

    /**
     * 修改纪念日（按 id 覆盖全部可编辑字段）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("admin:anniversary:update")
    @Log(module = "纪念日", operation = "修改纪念日", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody AnniversaryUpdateRequest updateRequest) {
        AnniversaryUpdateDTO updateDTO = anniversaryConverter.toUpdateDTO(updateRequest);
        anniversaryService.update(updateDTO);
    }

    /**
     * 修改纪念日显隐。
     *
     * @param request 显隐修改请求参数
     */
    @SaCheckPermission("admin:anniversary:update")
    @Log(module = "纪念日", operation = "修改纪念日显隐", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-hidden")
    public void changeHidden(@Valid @RequestBody AnniversaryChangeHiddenRequest request) {
        AnniversaryChangeHiddenDTO changeHiddenDTO = anniversaryConverter.toChangeHiddenDTO(request);
        anniversaryService.changeHidden(changeHiddenDTO);
    }

    /**
     * 批量逻辑删除纪念日。
     *
     * @param ids 纪念日ID集合（雪花ID字符串化传输）
     */
    @SaCheckPermission("admin:anniversary:delete")
    @Log(module = "纪念日", operation = "删除纪念日", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        anniversaryService.delete(List.of(ids));
    }
}
