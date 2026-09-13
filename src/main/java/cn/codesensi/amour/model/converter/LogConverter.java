package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.LogPageDTO;
import cn.codesensi.amour.model.entity.SysLog;
import cn.codesensi.amour.model.request.LogPageRequest;
import cn.codesensi.amour.model.response.LogPageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 系统日志转换器 —— {@link SysLog} 实体转 {@link LogPageResponse}（MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code LogConverterImpl} 为 Spring 组件）；
 * 两侧字段同名，由 MapStruct 自动映射，无需显式 {@code @Mapping}；
 * 请求参数（param）与响应结果（result）为脱敏截断后的大体量文本，随行下发供列表截断展示与详情查看。
 *
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LogConverter {

    /**
     * 将分页查询请求转换为分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    LogPageDTO toPageDTO(LogPageRequest request);

    /**
     * Page&lt;SysLog&gt; → Page&lt;LogPageResponse&gt;
     * （records 逐元素复用实体 → 行响应对象的映射规则）。
     *
     * @param page 日志实体分页
     * @return 日志行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<LogPageResponse> toPageResponse(Page<SysLog> page);

}
