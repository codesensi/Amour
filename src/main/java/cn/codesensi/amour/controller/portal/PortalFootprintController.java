package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.FootprintConverter;
import cn.codesensi.amour.model.dto.FootprintDTO;
import cn.codesensi.amour.model.response.PortalFootprintResponse;
import cn.codesensi.amour.service.FootprintService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户足迹地图相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供「足迹地图」到访记录的分页下发
 * （/portal/** 已在 RbacConst 公开清单整体放行）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/footprint")
public class PortalFootprintController {

    private final FootprintService footprintService;
    private final FootprintConverter footprintConverter;

    /**
     * 查询足迹分页（免登录）
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 全部未删除足迹，按到访日期升序;photoUrl 为免登录分发地址或 null
     */
    @GetMapping
    public Page<PortalFootprintResponse> page(BasePage page) {
        Page<FootprintDTO> itemPage = footprintService.pagePortal(page);
        return footprintConverter.toPortalPage(itemPage);
    }
}
