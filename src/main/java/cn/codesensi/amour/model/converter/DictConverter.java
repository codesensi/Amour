package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.DictDTO;
import cn.codesensi.amour.model.dto.DictGroupDTO;
import cn.codesensi.amour.model.dto.DictPageDTO;
import cn.codesensi.amour.model.dto.DictTypeDTO;
import cn.codesensi.amour.model.entity.SysDict;
import cn.codesensi.amour.model.request.DictPageRequest;
import cn.codesensi.amour.model.response.DictGroupResponse;
import cn.codesensi.amour.model.response.DictPageResponse;
import cn.codesensi.amour.model.response.DictResponse;
import cn.codesensi.amour.model.response.DictTypeResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 数据字典转换器 —— {@link SysDict} 实体转 {@link DictDTO}（MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code DictConvertImpl} 为 Spring 组件）；
 * 两侧字段（{@code dictValue}、{@code dictLabel}、{@code sort}）同名，
 * 由 MapStruct 自动映射，无需显式 {@code @Mapping}。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DictConverter {

    /**
     * 将字典实体转换为字典项 DTO。
     *
     * @param dict 字典实体
     * @return 字典项 DTO
     */
    DictDTO toDTO(SysDict dict);

    /**
     * 将字典实体列表转换为字典项 DTO 列表（逐元素复用 {@link #toDTO(SysDict)} 的映射规则）。
     *
     * @param dictList 字典实体列表
     * @return 字典项 DTO 列表
     */
    List<DictDTO> toListDTO(List<SysDict> dictList);

    /**
     * 将字典项 DTO 转换为响应对象。
     *
     * @param dictDTO 字典项 DTO
     * @return 字典项响应对象
     */
    DictResponse toResponse(DictDTO dictDTO);

    /**
     * 将字典项 DTO 列表转换为响应对象列表（逐元素复用 {@link #toResponse(DictDTO)} 的映射规则）。
     *
     * @param dictDTOList 字典项 DTO 列表
     * @return 字典项响应对象列表
     */
    List<DictResponse> toListResponse(List<DictDTO> dictDTOList);

    /**
     * 将字典分组 DTO 转换为分组响应对象（组内列表复用 {@link #toListResponse(List)} 的映射规则）。
     *
     * @param dictGroupDTO 字典分组 DTO
     * @return 分组响应对象
     */
    DictGroupResponse toGroupResponse(DictGroupDTO dictGroupDTO);

    /**
     * 将字典分组 DTO 列表转换为分组响应对象列表（逐元素复用 {@link #toGroupResponse(DictGroupDTO)} 的映射规则）。
     *
     * @param dictGroupDTOList 字典分组 DTO 列表
     * @return 分组响应对象列表
     */
    List<DictGroupResponse> toListGroupResponse(List<DictGroupDTO> dictGroupDTOList);

    /**
     * 将字典类型 DTO 转换为类型响应对象。
     *
     * @param dictTypeDTO 字典类型 DTO
     * @return 字典类型响应对象
     */
    DictTypeResponse toTypeResponse(DictTypeDTO dictTypeDTO);

    /**
     * 将字典类型 DTO 列表转换为类型响应对象列表（逐元素复用 {@link #toTypeResponse(DictTypeDTO)} 的映射规则）。
     *
     * @param dictTypeDTOList 字典类型 DTO 列表
     * @return 字典类型响应对象列表
     */
    List<DictTypeResponse> toListTypeResponse(List<DictTypeDTO> dictTypeDTOList);

    /**
     * 字典分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    DictPageDTO toPageDTO(DictPageRequest request);

    /**
     * Page&lt;SysDict&gt; → Page&lt;DictPageResponse&gt;
     * （records 逐元素复用实体 → 行响应对象的映射规则）。
     *
     * @param page 字典实体分页
     * @return 字典行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<DictPageResponse> toPageResponse(Page<SysDict> page);

}
