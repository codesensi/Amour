package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.MomentsConverter;
import cn.codesensi.amour.model.dto.MomentsDTO;
import cn.codesensi.amour.model.response.PortalMomentsResponse;
import cn.codesensi.amour.service.MomentsService;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点点滴滴门户相关接口 前端控制器（免登录）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/moments")
public class PortalMomentsController {

    private final MomentsService momentsService;

    private final MomentsConverter momentsConverter;

    /**
     * 门户点点滴滴分页（免登录,仅显示状态）。
     * <p>
     * 排序为 sort 升序 → 记录日期降序 → id 降序，
     * 作者展示信息由服务层批量回填。
     *
     * @return 点点滴滴分页结果
     */
    @GetMapping("/page")
    public Page<PortalMomentsResponse> page(BasePage page) {
        Page<MomentsDTO> itemPage = momentsService.pagePortal(page);
        return momentsConverter.toPortalPage(itemPage);
    }

    /**
     * 点点滴滴文章详情（免登录,仅显示状态;未命中返回 data null）。
     *
     * @param id 文章ID
     * @return 点点滴滴文章详情;未命中返回 null
     */
    @GetMapping("/detail/{id}")
    public PortalMomentsResponse detail(@PathVariable Long id) {
        MomentsDTO itemDTO = momentsService.detail(id);
        if (ObjUtil.isNull(itemDTO)) {
            return null;
        }
        return momentsConverter.toPortalResponse(itemDTO);
    }
}
