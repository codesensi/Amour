package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.VisitConverter;
import cn.codesensi.amour.model.dto.VisitTotalDTO;
import cn.codesensi.amour.model.response.VisitTotalResponse;
import cn.codesensi.amour.service.PortalVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户访问统计相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供访问量的上报与累计查询
 * （/portal/** 已在 RbacConst 公开清单整体放行）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/visit")
public class PortalVisitController {

    private final PortalVisitService portalVisitService;

    private final VisitConverter visitConverter;

    /**
     * 上报一次访问（免登录；去重由前端按「访客 + 日」控制）。
     */
    @PostMapping("/report")
    public void report() {
        portalVisitService.report();
    }

    /**
     * 查询累计访问统计（免登录）
     *
     * @return 累计 PV 与 UV
     */
    @GetMapping("/total")
    public VisitTotalResponse total() {
        VisitTotalDTO visitTotalDTO = portalVisitService.total();
        return visitConverter.toResponse(visitTotalDTO);
    }
}
