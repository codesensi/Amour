package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.DashboardConverter;
import cn.codesensi.amour.model.dto.AnnualReviewDTO;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.response.*;
import cn.codesensi.amour.service.DashboardService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 最近回忆时间线（画册照片/点点滴滴/情侣日志混排,固定返回最新条数）。
     *
     * @return 时间线条目响应分页结果
     */
    @GetMapping("/timeline")
    public Page<TimelineItemResponse> timeline() {
        return dashboardConverter.toTimelineResponse(dashboardService.timeline());
    }

    /**
     * 访问趋势（按日粒度 PV/UV，日期区间逐日补齐）。
     *
     * @param days 统计最近天数（7-365，越界自动收敛；缺省 30）
     * @return 访问趋势条目响应列表（按日期升序）
     */
    @GetMapping("/visit-trend")
    public List<DashboardVisitTrendItemResponse> visitTrend(
            @RequestParam(defaultValue = "30") int days) {
        return dashboardConverter.toVisitTrendResponse(dashboardService.visitTrend(days));
    }

    /**
     * 留言地区分布（审核通过口径，按条数降序）。
     *
     * @param top 返回条数上限（1-20，越界自动收敛；缺省 10）
     * @return 地区分布条目响应列表
     */
    @GetMapping("/message-region")
    public List<DashboardMessageRegionResponse> messageRegion(
            @RequestParam(defaultValue = "10") int top) {
        return dashboardConverter.toMessageRegionResponse(dashboardService.messageRegion(top));
    }

    /**
     * 年度恋爱回顾（聚合各业务表当年产生量、新增城市、心情画像与精选回忆）。
     *
     * @param year 统计年份（1970-2100，越界自动收敛）
     * @return 年度回顾响应
     */
    @GetMapping("/annual-review/{year}")
    public AnnualReviewResponse annualReview(@PathVariable int year) {
        AnnualReviewDTO reviewDTO = dashboardService.annualReview(year);
        return dashboardConverter.toAnnualReviewResponse(reviewDTO);
    }

}
