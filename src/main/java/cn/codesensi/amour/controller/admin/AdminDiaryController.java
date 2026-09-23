package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.DiaryConverter;
import cn.codesensi.amour.model.dto.DiaryDTO;
import cn.codesensi.amour.model.request.DiaryInsertRequest;
import cn.codesensi.amour.model.request.DiaryPageRequest;
import cn.codesensi.amour.model.request.DiaryUpdateRequest;
import cn.codesensi.amour.model.response.DiaryPageResponse;
import cn.codesensi.amour.service.DiaryService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 情侣日记管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/diary")
public class AdminDiaryController {

    private final DiaryService diaryService;

    private final DiaryConverter diaryConverter;

    /**
     * 分页查询情侣日记（管理端，完整字段）。
     * <p>
     * 记录人/记录日期/心情为精确匹配，条件缺省时自动忽略；
     * 排序为记录日期降序 → id 降序。
     *
     * @param pageRequest 分页查询参数
     * @return 情侣日记分页结果
     */
    @SaCheckPermission("admin:diary:page")
    @GetMapping("/page")
    public Page<DiaryPageResponse> page(@Valid DiaryPageRequest pageRequest) {
        Page<DiaryDTO> itemPage = diaryService.pageAdmin(diaryConverter.toPageDTO(pageRequest));
        return diaryConverter.toPageResponse(itemPage);
    }

    /**
     * 新增情侣日记（记录人取当前登录人）。
     *
     * @param insertRequest 新增请求参数
     */
    @SaCheckPermission("admin:diary:insert")
    @Log(module = "情侣日记", operation = "新增情侣日记", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody DiaryInsertRequest insertRequest) {
        diaryService.insert(diaryConverter.toInsertDTO(insertRequest));
    }

    /**
     * 修改情侣日记（按 id 覆盖可编辑字段,记录人归属不可变）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("admin:diary:update")
    @Log(module = "情侣日记", operation = "修改情侣日记", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody DiaryUpdateRequest updateRequest) {
        diaryService.update(diaryConverter.toUpdateDTO(updateRequest));
    }

    /**
     * 批量逻辑删除情侣日记。
     *
     * @param ids 日记ID集合（雪花ID字符串化传输）
     */
    @SaCheckPermission("admin:diary:delete")
    @Log(module = "情侣日记", operation = "删除情侣日记", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        diaryService.delete(List.of(ids));
    }
}
