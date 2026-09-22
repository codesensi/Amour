package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import com.mybatisflex.core.paginate.Page;

/**
 * 管理端首页数据聚合 Service。
 * <p>
 * 只读聚合层：以原生 SQL 直接统计/取样各 portal 业务表（含尚未提供模块接口的表），
 * 为首页提供单次请求可得的概览数据，避免前端跨模块瀑布请求。
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
     * 最近回忆时间线（画册照片/点点滴滴/情侣日志按时间倒序混排）。
     *
     * @param timelineDTO 分页参数 DTO
     * @return 时间线条目 DTO 分页
     */
    Page<DashboardTimelineItemDTO> timeline(DashboardTimelineDTO timelineDTO);

}
