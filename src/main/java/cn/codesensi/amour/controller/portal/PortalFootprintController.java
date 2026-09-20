package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.FootprintConverter;
import cn.codesensi.amour.model.dto.FootprintDTO;
import cn.codesensi.amour.model.response.FootprintResponse;
import cn.codesensi.amour.service.FootprintService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * @param page 分页参数（pageNumber/pageSize，页码 1 起且每页不超过 500）
     * @return 全部未删除足迹，按到访日期升序;photoUrl 为免登录分发地址或 null
     */
    @GetMapping("/page")
    public Page<FootprintResponse> page(@Valid BasePage page) {
        Page<FootprintDTO> itemPage = footprintService.pagePortal(page);
        return footprintConverter.toPortalPage(itemPage);
    }

    /**
     * 查询足迹地图全量点集（免登录）
     *
     * @return 全部未删除足迹，按到访日期升序;防御性上限 1000 条
     */
    @GetMapping("/list/map-points")
    public List<FootprintResponse> listMapPoints() {
        List<FootprintDTO> footprintDTOS = footprintService.listMapPoints();
        return footprintConverter.toPortalResponseList(footprintDTOS);
    }
}
