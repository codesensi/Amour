package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysDictData;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.DictPageResponse;
import cn.codesensi.amour.model.response.DictTypeResponse;
import cn.codesensi.amour.service.SysDictDataService;
import cn.codesensi.amour.service.SysDictTypeService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/dict")
public class SysDictController {

    private final SysDictTypeService sysDictTypeService;
    private final SysDictDataService sysDictDataService;
    private final DictConverter dictConverter;

    /**
     * 查询全部字典类型（含组内条目数；字典管理页左侧类型列表的数据源）。
     *
     * @return 字典类型列表
     */
    @SaCheckPermission("system:dict:page")
    @GetMapping("/type/list")
    public List<DictTypeResponse> typeList() {
        List<DictTypeDTO> dictTypeList = sysDictTypeService.listTypes();
        return dictConverter.toListTypeResponse(dictTypeList);
    }

    /**
     * 新增字典类型。
     * <p>校验字典编码全生命周期唯一。
     *
     * @param request 新增字典类型请求参数
     */
    @SaCheckPermission("system:dict:insert")
    @Log(module = "字典管理", operation = "新增字典类型", type = LogTypeEnum.INSERT)
    @PostMapping("/type/insert")
    public void insertType(@Valid @RequestBody DictTypeInsertRequest request) {
        DictTypeInsertDTO insertDTO = dictConverter.toInsertDTO(request);
        sysDictTypeService.insertType(insertDTO);
    }

    /**
     * 修改字典类型。
     * <p>字典编码与内置标识不可修改，仅允许维护名称与备注。
     *
     * @param request 修改字典类型请求参数
     */
    @SaCheckPermission("system:dict:update")
    @Log(module = "字典管理", operation = "修改字典类型", type = LogTypeEnum.UPDATE)
    @PutMapping("/type/update")
    public void updateType(@Valid @RequestBody DictTypeUpdateRequest request) {
        DictTypeUpdateDTO updateDTO = dictConverter.toUpdateDTO(request);
        sysDictTypeService.updateType(updateDTO);
    }

    /**
     * 批量删除字典类型（内置类型禁删、类型下存在条目时禁删，整批失败）。
     * <p>路径参数支持英文逗号分隔的多个ID，如 /sys/dict/type/delete/1,2,3。
     *
     * @param ids 字典类型ID列表
     */
    @SaCheckPermission("system:dict:delete")
    @Log(module = "字典管理", operation = "删除字典类型", type = LogTypeEnum.DELETE)
    @DeleteMapping("/type/delete/{ids}")
    public void deleteType(@PathVariable Long[] ids) {
        sysDictTypeService.deleteType(List.of(ids));
    }

    /**
     * 分页查询字典条目（登录态；字典管理页右侧行数据源，含禁用条目与完整字段）。
     * <p>编码、名称、值为模糊匹配，状态为精确匹配，条件缺省时自动忽略。
     *
     * @param dictDataPageRequest 分页查询参数
     * @return 字典条目分页结果(按编码升序 → 组内 sort 升序；dictName 为类型名展示字段)
     */
    @SaCheckPermission("system:dict:page")
    @GetMapping("/data/page")
    public Page<DictPageResponse> dataPage(@Valid DictDataPageRequest dictDataPageRequest) {
        DictDataPageDTO pageDTO = dictConverter.toPageDTO(dictDataPageRequest);
        Page<SysDictData> dictPage = sysDictDataService.dataPage(pageDTO);
        return dictConverter.toPageResponse(dictPage);
    }

    /**
     * 新增字典条目。
     * <p>校验字典类型存在、同编码下字典值唯一；成功后失效该编码的字典缓存。
     *
     * @param request 新增字典条目请求参数
     */
    @SaCheckPermission("system:dict:insert")
    @Log(module = "字典管理", operation = "新增字典条目", type = LogTypeEnum.INSERT)
    @PostMapping("/data/insert")
    public void dataInsert(@Valid @RequestBody DictDataInsertRequest request) {
        DictDataInsertDTO insertDTO = dictConverter.toInsertDTO(request);
        sysDictDataService.dataInsert(insertDTO);
    }

    /**
     * 修改字典条目。
     * <p>字典编码与内置标识不可修改，状态经启停接口单独维护；内置条目锁定字典值；
     * 成功后失效该编码的字典缓存。
     *
     * @param request 修改字典条目请求参数
     */
    @SaCheckPermission("system:dict:update")
    @Log(module = "字典管理", operation = "修改字典条目", type = LogTypeEnum.UPDATE)
    @PutMapping("/data/update")
    public void dataUpdate(@Valid @RequestBody DictDataUpdateRequest request) {
        DictDataUpdateDTO updateDTO = dictConverter.toUpdateDTO(request);
        sysDictDataService.dataUpdate(updateDTO);
    }

    /**
     * 修改字典条目状态。
     * <p>内置条目不允许更改状态（始终启用）；成功后失效该编码的字典缓存。
     *
     * @param request 修改字典状态请求参数
     */
    @SaCheckPermission("system:dict:update")
    @Log(module = "字典管理", operation = "修改字典状态", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-status")
    public void changeStatus(@Valid @RequestBody DictChangeStatusRequest request) {
        DictChangeStatusDTO changeStatusDTO = dictConverter.toChangeStatusDTO(request);
        sysDictDataService.changeStatus(changeStatusDTO);
    }

    /**
     * 批量删除字典条目（内置条目禁删，整批失败）。
     * <p>路径参数支持英文逗号分隔的多个ID，如 /sys/dict/data/delete/1,2,3。
     *
     * @param ids 字典条目ID列表
     */
    @SaCheckPermission("system:dict:delete")
    @Log(module = "字典管理", operation = "删除字典条目", type = LogTypeEnum.DELETE)
    @DeleteMapping("/data/delete/{ids}")
    public void dataDelete(@PathVariable Long[] ids) {
        sysDictDataService.dataDelete(List.of(ids));
    }
}
