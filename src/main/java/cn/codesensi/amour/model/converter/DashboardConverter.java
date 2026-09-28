package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.AnnualReviewDTO;
import cn.codesensi.amour.model.dto.DashboardMessageRegionDTO;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.dto.DashboardVisitTrendItemDTO;
import cn.codesensi.amour.model.response.AnnualReviewResponse;
import cn.codesensi.amour.model.response.DashboardMessageRegionResponse;
import cn.codesensi.amour.model.response.DashboardSummaryResponse;
import cn.codesensi.amour.model.response.DashboardVisitTrendItemResponse;
import cn.codesensi.amour.model.response.TimelineItemResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 首页数据聚合转换器（MapStruct 编译期生成实现类）。
 * <p>
 * 承接 DTO → 响应层（聚合结果）的转换；
 * 嵌套的纪念日条目复用 {@link AnniversaryConverter} 的既有映射规则。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AnniversaryConverter.class)
public interface DashboardConverter {

    /**
     * 首页数据聚合 DTO → 聚合响应。
     *
     * @param dto 首页数据聚合 DTO
     * @return 首页数据聚合响应
     */
    DashboardSummaryResponse toSummaryResponse(DashboardSummaryDTO dto);

    /**
     * Page&lt;DashboardTimelineItemDTO&gt; → Page&lt;TimelineItemResponse&gt;
     * （records 逐元素复用条目 DTO → 响应对象的映射规则）。
     *
     * @param page 时间线条目 DTO 分页
     * @return 时间线条目响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<TimelineItemResponse> toTimelineResponse(Page<DashboardTimelineItemDTO> page);

    /**
     * 访问趋势条目 DTO 列表 → 响应列表。
     *
     * @param items 访问趋势条目 DTO 列表
     * @return 访问趋势条目响应列表
     */
    List<DashboardVisitTrendItemResponse> toVisitTrendResponse(List<DashboardVisitTrendItemDTO> items);

    /**
     * 留言地区分布条目 DTO 列表 → 响应列表。
     *
     * @param items 地区分布条目 DTO 列表
     * @return 地区分布条目响应列表
     */
    List<DashboardMessageRegionResponse> toMessageRegionResponse(List<DashboardMessageRegionDTO> items);

    /**
     * 年度回顾 DTO → 响应（highlights 逐元素复用条目 DTO → 响应对象的映射规则）。
     *
     * @param dto 年度回顾 DTO
     * @return 年度回顾响应
     */
    AnnualReviewResponse toAnnualReviewResponse(AnnualReviewDTO dto);

}
