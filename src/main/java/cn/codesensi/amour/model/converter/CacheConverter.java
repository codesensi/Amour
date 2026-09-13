package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.CacheDTO;
import cn.codesensi.amour.model.dto.CacheEntryDTO;
import cn.codesensi.amour.model.dto.CacheStatsDTO;
import cn.codesensi.amour.model.response.CacheEntryResponse;
import cn.codesensi.amour.model.response.CacheResponse;
import cn.codesensi.amour.model.response.CacheStatsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 缓存转换器 —— {@link CacheDTO} 及其嵌套对象转 Response（MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code CacheConverterImpl} 为 Spring 组件）；
 * 同名字段由 MapStruct 自动映射。
 *
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CacheConverter {

    /**
     * 缓存 DTO → 缓存内容响应（stats/entries 嵌套对象逐级复用下述映射规则）。
     *
     * @param dto 缓存 DTO
     * @return 缓存内容响应对象
     */
    CacheResponse toResponse(CacheDTO dto);

    /**
     * 缓存命中统计 DTO → 缓存命中统计响应。
     *
     * @param dto 缓存命中统计 DTO
     * @return 缓存命中统计响应对象
     */
    CacheStatsResponse toStatsResponse(CacheStatsDTO dto);

    /**
     * 缓存条目 DTO → 缓存条目响应。
     *
     * @param dto 缓存条目 DTO
     * @return 缓存条目响应对象
     */
    CacheEntryResponse toEntryResponse(CacheEntryDTO dto);

    /**
     * 缓存 DTO 列表 → 缓存内容响应列表（逐元素复用 {@link #toResponse(CacheDTO)}）。
     *
     * @param dtoList 缓存 DTO 列表
     * @return 缓存内容响应对象列表
     */
    List<CacheResponse> toResponseList(List<CacheDTO> dtoList);

}
