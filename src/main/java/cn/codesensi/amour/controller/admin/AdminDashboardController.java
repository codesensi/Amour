package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.DashboardConverter;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.request.DashboardTimelineRequest;
import cn.codesensi.amour.model.response.DashboardSummaryResponse;
import cn.codesensi.amour.model.response.TimelineItemResponse;
import cn.codesensi.amour.service.DashboardService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端首页数据聚合相关接口 前端控制器。
 * <p>
 * 登录即可访问（首页对全部登录用户开放，不挂权限码）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;
    private final DashboardConverter dashboardConverter;

    /**
     * 首页数据聚合。
     *
     * @return 首页数据聚合响应
     */
    @GetMapping("/summary")
    public DashboardSummaryResponse summary() {
        DashboardSummaryDTO summaryDTO = dashboardService.summary();
        return dashboardConverter.toSummaryResponse(summaryDTO);
    }

    /**
     * 最近回忆时间线（画册照片/点点滴滴/情侣日志混排）。
     *
     * @param request 分页查询参数
     * @return 时间线条目响应分页结果
     */
    @GetMapping("/timeline")
    public Page<TimelineItemResponse> timeline(@Valid DashboardTimelineRequest request) {
        DashboardTimelineDTO timelineDTO = dashboardConverter.toTimelineDTO(request);
        Page<DashboardTimelineItemDTO> timeline = dashboardService.timeline(timelineDTO);
        return dashboardConverter.toTimelineResponse(timeline);
    }

}
