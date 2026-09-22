package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.request.DashboardTimelineRequest;
import cn.codesensi.amour.model.response.DashboardSummaryResponse;
import cn.codesensi.amour.model.response.TimelineItemResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 首页数据聚合转换器（MapStruct 编译期生成实现类）。
 * <p>
 * 承接请求层 → DTO（分页参数）、DTO → 响应层（聚合结果）的双向转换；
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
     * 时间线分页请求 → 时间线分页参数 DTO。
     *
     * @param request 时间线分页请求
     * @return 时间线分页参数 DTO
     */
    DashboardTimelineDTO toTimelineDTO(DashboardTimelineRequest request);

    /**
     * Page&lt;DashboardTimelineItemDTO&gt; → Page&lt;TimelineItemResponse&gt;
     * （records 逐元素复用条目 DTO → 响应对象的映射规则）。
     *
     * @param page 时间线条目 DTO 分页
     * @return 时间线条目响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<TimelineItemResponse> toTimelineResponse(Page<DashboardTimelineItemDTO> page);

}
