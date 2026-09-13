package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.DictGroupDTO;
import cn.codesensi.amour.model.response.DictGroupResponse;
import cn.codesensi.amour.service.SysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门户数据字典相关接口 前端控制器
 * <p>
 * 面向门户免登录场景，按需批量下发启用中的字典项，供前端下拉框与枚举展示统一消费。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalDictController {

    private final SysDictService sysDictService;
    private final DictConverter dictConverter;

    /**
     * 按字典编码集合批量查询启用中的字典项列表（免登录；供前端下拉框与枚举展示统一消费）。
     * <p>逗号分隔传输（?codes=gender,enable），Spring 默认按逗号拆分为 List&lt;String&gt;。
     *
     * @param codes 字典编码集合（如 gender、enable）；为空时返回空分组
     * @return 字典分组列表（每组含 dictCode 与组内条目，条目按 sort 升序）；无命中时返回空列表
     */
    @GetMapping("/dict/list-by-codes")
    public List<DictGroupResponse> listByCodes(@RequestParam(value = "codes", required = false) List<String> codes) {
        List<DictGroupDTO> dictGroups = sysDictService.listByCodes(codes);
        return dictConverter.toListGroupResponse(dictGroups);
    }
}
