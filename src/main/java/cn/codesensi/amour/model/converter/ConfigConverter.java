package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.model.response.ConfigResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 系统配置转换器 —— {@link SysConfig} 实体转 {@link ConfigDTO}（MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code ConfigConvertImpl} 为 Spring 组件）；
 * 两侧字段（{@code configKey}、{@code configValue}、{@code valueType}、{@code configGroup}）同名，
 * 由 MapStruct 自动映射，无需显式 {@code @Mapping}。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigConverter {

    /**
     * 将配置实体转换为 DTO。
     *
     * @param config 配置实体
     * @return 配置 DTO
     */
    ConfigDTO toDTO(SysConfig config);

    /**
     * 将配置实体列表转换为 DTO 列表（逐元素复用 {@link #toDTO(SysConfig)} 的映射规则）。
     *
     * @param configs 配置实体列表
     * @return 配置 DTO 列表
     */
    List<ConfigDTO> toDTOList(List<SysConfig> configs);

    /**
     * 将配置 DTO 转换为响应对象。
     *
     * @param configDTO 配置 DTO
     * @return 配置响应对象
     */
    ConfigResponse toResponse(ConfigDTO configDTO);

    /**
     * 将配置 DTO 列表转换为响应对象列表（逐元素复用 {@link #toResponse(ConfigDTO)} 的映射规则）。
     *
     * @param configDTOs 配置 DTO 列表
     * @return 配置响应对象列表
     */
    List<ConfigResponse> toResponseList(List<ConfigDTO> configDTOs);

}
