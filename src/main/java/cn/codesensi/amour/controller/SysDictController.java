package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysDict;
import cn.codesensi.amour.model.request.DictChangeStatusRequest;
import cn.codesensi.amour.model.request.DictInsertRequest;
import cn.codesensi.amour.model.request.DictPageRequest;
import cn.codesensi.amour.model.request.DictUpdateRequest;
import cn.codesensi.amour.model.response.DictGroupResponse;
import cn.codesensi.amour.model.response.DictPageResponse;
import cn.codesensi.amour.model.response.DictTypeResponse;
import cn.codesensi.amour.service.SysDictService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/dict")
public class SysDictController {

    private final SysDictService sysDictService;
    private final DictConverter dictConverter;

    /**
     * 查询全部字典类型（按编码聚合，含条目数；字典管理页左侧类型列表的数据源）。
     *
     * @return 字典类型列表
     */
    @SaCheckPermission("system:dict:page")
    @GetMapping("/type-list")
    public List<DictTypeResponse> typeList() {
        List<DictTypeDTO> dictTypeDTOS = sysDictService.listTypes();
        return dictConverter.toListTypeResponse(dictTypeDTOS);
    }

    /**
     * 按字典编码集合批量查询启用中的字典项列表（免登录；供前端下拉框与枚举展示统一消费）。
     * <p>逗号分隔传输（?codes=gender,enable），Spring 默认按逗号拆分为 List&lt;String&gt;。
     *
     * @param codes 字典编码集合（如 gender、enable）；为空时返回空分组
     * @return 字典分组列表（每组含 dictCode 与组内条目，条目按 sort 升序）；无命中时返回空列表
     */
    @SaIgnore
    @GetMapping("/list-by-codes")
    public List<DictGroupResponse> listByCodes(@RequestParam(value = "codes", required = false) List<String> codes) {
        List<DictGroupDTO> dictGroups = sysDictService.listByCodes(codes);
        return dictConverter.toListGroupResponse(dictGroups);
    }

    /**
     * 分页查询字典条目（登录态；字典管理页右侧行数据源，含禁用条目与完整字段）。
     * <p>编码、名称、值为模糊匹配，状态为精确匹配，条件缺省时自动忽略。
     *
     * @param dictPageRequest 分页查询参数
     * @return 字典条目分页结果(按编码升序 → 组内 sort 升序)
     */
    @SaCheckPermission("system:dict:page")
    @GetMapping("/page")
    public Page<DictPageResponse> page(@Valid DictPageRequest dictPageRequest) {
        DictPageDTO pageDTO = dictConverter.toPageDTO(dictPageRequest);
        Page<SysDict> dictPage = sysDictService.page(pageDTO);
        return dictConverter.toPageResponse(dictPage);
    }

    /**
     * 新增字典条目。
     * <p>校验同编码下字典值唯一；成功后失效该编码的字典缓存。
     *
     * @param request 新增字典条目请求参数
     */
    @SaCheckPermission("system:dict:insert")
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody DictInsertRequest request) {
        DictInsertDTO insertDTO = dictConverter.toInsertDTO(request);
        sysDictService.insert(insertDTO);
    }

    /**
     * 修改字典条目。
     * <p>字典编码与内置标识不可修改，状态经启停接口单独维护；内置条目锁定字典值；
     * 成功后失效该编码的字典缓存。
     *
     * @param request 修改字典条目请求参数
     */
    @SaCheckPermission("system:dict:update")
    @PutMapping("/update")
    public void update(@Valid @RequestBody DictUpdateRequest request) {
        DictUpdateDTO updateDTO = dictConverter.toUpdateDTO(request);
        sysDictService.update(updateDTO);
    }

    /**
     * 修改字典条目状态。
     * <p>内置条目不允许更改状态（始终启用）；成功后失效该编码的字典缓存。
     *
     * @param request 修改字典状态请求参数
     */
    @SaCheckPermission("system:dict:update")
    @PutMapping("/change-status")
    public void changeStatus(@Valid @RequestBody DictChangeStatusRequest request) {
        DictChangeStatusDTO changeStatusDTO = dictConverter.toChangeStatusDTO(request);
        sysDictService.changeStatus(changeStatusDTO);
    }

    /**
     * 批量删除字典条目（内置条目禁删，整批失败）。
     * <p>路径参数支持英文逗号分隔的多个ID，如 /sys/dict/delete/1,2,3。
     *
     * @param ids 字典条目ID列表
     */
    @SaCheckPermission("system:dict:delete")
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        sysDictService.delete(List.of(ids));
    }
}