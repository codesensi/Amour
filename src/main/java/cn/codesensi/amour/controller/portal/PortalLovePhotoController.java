package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.LovePhotoConverter;
import cn.codesensi.amour.model.dto.LovePhotoDTO;
import cn.codesensi.amour.model.request.PortalLovePhotoPageRequest;
import cn.codesensi.amour.model.response.LovePhotoArchiveItemResponse;
import cn.codesensi.amour.model.response.LovePhotoResponse;
import cn.codesensi.amour.service.LovePhotoService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    private final LovePhotoService lovePhotoService;
    private final LovePhotoConverter lovePhotoConverter;

    /**
     * 查询恋爱画册照片分页（免登录）
     * <p>
     * 年份/标签过滤均可缺省（缺省时与原门户口径一致），仅返回显隐为「显示」的照片，
     * 按 sort 升序;tags 为标签数组，可能为空。
     *
     * @param pageRequest 门户分页查询参数（含年份/标签过滤，可空）
     * @return 仅含显隐为「显示」的照片分页结果
     */
    @GetMapping("/page")
    public Page<LovePhotoResponse> page(@Valid PortalLovePhotoPageRequest pageRequest) {
        Page<LovePhotoDTO> itemPage = lovePhotoService.pagePortal(pageRequest);
        return lovePhotoConverter.toPortalPage(itemPage);
    }

    /**
     * 查询恋爱画册年份归档（免登录）
     *
     * @return 年份归档条目列表（按年份降序）;画册为空时为空列表
     */
    @GetMapping("/archive")
    public List<LovePhotoArchiveItemResponse> archive() {
        return lovePhotoConverter.toArchiveResponse(lovePhotoService.archive());
    }

    /**
     * 查询恋爱画册封面照片（免登录）
     *
     * @return 显隐为「显示」的照片中 sort 首位;画册为空时 data 为 null
     */
    @GetMapping("/cover")
    public LovePhotoResponse cover() {
        LovePhotoDTO portalCover = lovePhotoService.getPortalCover();
        return lovePhotoConverter.toPortalResponse(portalCover);
    }
}
