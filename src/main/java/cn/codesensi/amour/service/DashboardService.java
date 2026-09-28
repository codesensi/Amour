package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.AnnualReviewDTO;
import cn.codesensi.amour.model.dto.DashboardMessageRegionDTO;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.dto.DashboardVisitTrendItemDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 管理端首页数据聚合 Service。
 * <p>
 * 只读聚合层：以实体 Mapper 强类型查询统计/取样各 portal 业务表,
 * 为首页提供单次请求可得的概览数据,避免前端跨模块瀑布请求。
 *
 * @author codesensi
 * @since 1.0
 */
public interface DashboardService {

    /**
     * 首页数据聚合。
     *
     * @return 概览数据 DTO（各字段均可为 null，由响应层按空状态渲染）
     */
    DashboardSummaryDTO summary();

    /**
     * 最近回忆时间线（画册照片/点点滴滴/情侣日志按时间倒序混排,仅取最新条数）。
     *
     * @return 时间线条目 DTO 分页
     */
    Page<DashboardTimelineItemDTO> timeline();

    /**
     * 访问趋势（按日粒度 PV/UV,日期区间逐日补齐,无访问的日期计 0）。
     *
     * @param days 统计最近天数（7-365,越界自动收敛到边界值）
     * @return 访问趋势条目 DTO 列表（按日期升序）
     */
    List<DashboardVisitTrendItemDTO> visitTrend(int days);

    /**
     * 留言地区分布（按审核通过的留言的 IP 归属地聚合，无法识别的归并为「未知」）。
     *
     * @param top 返回条数上限（1-20,越界自动收敛到边界值）
     * @return 地区分布条目 DTO 列表（按条数降序）
     */
    List<DashboardMessageRegionDTO> messageRegion(int top);

    /**
     * 年度恋爱回顾（聚合各业务表在指定年份的产生量与画像信息）。
     *
     * @param year 统计年份（1970-2100,越界自动收敛到边界值）
     * @return 年度回顾 DTO
     */
    AnnualReviewDTO annualReview(int year);

}
