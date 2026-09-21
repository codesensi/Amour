package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.AnniversaryConverter;
import cn.codesensi.amour.model.dto.AnniversaryDTO;
import cn.codesensi.amour.model.response.PortalAnniversaryResponse;
import cn.codesensi.amour.service.AnniversaryService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户纪念日相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供「纪念日」的分页下发
 * （/portal/** 已在 RbacConst 公开清单整体放行）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/anniversary")
public class PortalAnniversaryController {

    private final AnniversaryService anniversaryService;
    private final AnniversaryConverter anniversaryConverter;

    /**
     * 查询纪念日分页（免登录）。
     * <p>
     * 排序为下一次发生日升序（首条即最近纪念日）。
     *
     * @param page 分页参数
     * @return 纪念日分页结果
     */
    @GetMapping("/page")
    public Page<PortalAnniversaryResponse> page(@Valid BasePage page) {
        return anniversaryConverter.toPortalPage(anniversaryService.pagePortal(page));
    }

    /**
     * 查询下一次发生的纪念日（免登录，首页「下一个纪念日」卡片专用）。
     *
     * @return 最近一条纪念日；无数据时为 null
     */
    @GetMapping("/next")
    public PortalAnniversaryResponse next() {
        AnniversaryDTO next = anniversaryService.next();
        return anniversaryConverter.toResponse(next);
    }
}
