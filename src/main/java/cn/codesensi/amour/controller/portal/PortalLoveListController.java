package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.LoveListConverter;
import cn.codesensi.amour.model.dto.LoveListItemDTO;
import cn.codesensi.amour.model.response.PortalLoveListResponse;
import cn.codesensi.amour.service.LoveListService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户恋爱清单相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供「恋爱清单」的分页下发
 * （仅显隐为「显示」的清单项；/portal/** 已在 RbacConst 公开清单整体放行）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/love-list")
public class PortalLoveListController {

    private final LoveListService loveListService;

    private final LoveListConverter loveListConverter;

    /**
     * 查询恋爱清单分页（免登录）。
     *
     * @param page 分页参数（pageNumber/pageSize，页码 1 起且每页不超过 500）
     * @return 仅含显隐为「显示」的清单项，按 sort 升序;done 为布尔形态
     */
    @GetMapping
    public Page<PortalLoveListResponse> page(@Valid BasePage page) {
        Page<LoveListItemDTO> itemPage = loveListService.pagePortal(page);
        return loveListConverter.toPortalPage(itemPage);
    }
}
