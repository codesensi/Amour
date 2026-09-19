package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.LovePhotoConverter;
import cn.codesensi.amour.model.dto.LovePhotoDTO;
import cn.codesensi.amour.model.response.PortalLovePhotoResponse;
import cn.codesensi.amour.service.PortalLovePhotoService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户恋爱画册相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供「恋爱画册」照片的分页下发
 * （仅显隐为「显示」的照片；/portal/** 已在 RbacConst 公开清单整体放行）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/love-photo")
public class PortalLovePhotoController {

    private final PortalLovePhotoService portalLovePhotoService;
    private final LovePhotoConverter lovePhotoConverter;

    /**
     * 查询恋爱画册照片分页（免登录）
     *
     * @param page 分页参数(pageNumber/pageSize)
     * @return 仅含显隐为「显示」的照片,按 sort 升序;tags 为标签数组,可能为空
     */
    @GetMapping("/page")
    public Page<PortalLovePhotoResponse> page(BasePage page) {
        Page<LovePhotoDTO> itemPage = portalLovePhotoService.pagePortal(page);
        return lovePhotoConverter.toPortalPage(itemPage);
    }
}
