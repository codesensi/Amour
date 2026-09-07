package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.DictGroupDTO;
import cn.codesensi.amour.model.dto.DictPageDTO;
import cn.codesensi.amour.model.dto.DictTypeDTO;
import cn.codesensi.amour.model.entity.SysDict;
import cn.codesensi.amour.model.request.DictPageRequest;
import cn.codesensi.amour.model.response.DictGroupResponse;
import cn.codesensi.amour.model.response.DictPageResponse;
import cn.codesensi.amour.model.response.DictTypeResponse;
import cn.codesensi.amour.service.SysDictService;
import cn.dev33.satoken.annotation.SaIgnore;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 查询全部字典类型（按编码聚合,含条目数;字典管理页左侧类型列表的数据源）。
     *
     * @return 字典类型列表
     */
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
     * @return 字典分组列表（每组含 dictCode 与组内条目,条目按 sort 升序）；无命中时返回空列表
     */
    @SaIgnore
    @GetMapping("/list-by-codes")
    public List<DictGroupResponse> listByCodes(@RequestParam(value = "codes", required = false) List<String> codes) {
        List<DictGroupDTO> dictGroups = sysDictService.listByCodes(codes);
        return dictConverter.toListGroupResponse(dictGroups);
    }

    /**
     * 分页查询字典条目（登录态;字典管理页右侧行数据源,含禁用条目与完整字段）。
     * <p>编码、名称、值为模糊匹配,状态为精确匹配,条件缺省时自动忽略。
     *
     * @param dictPageRequest 分页查询参数
     * @return 字典条目分页结果(按编码升序 → 组内 sort 升序)
     */
    @GetMapping("/page")
    public Page<DictPageResponse> page(@Valid DictPageRequest dictPageRequest) {
        DictPageDTO pageDTO = dictConverter.toPageDTO(dictPageRequest);
        Page<SysDict> dictPage = sysDictService.page(pageDTO);
        return dictConverter.toPageResponse(dictPage);
    }
}
