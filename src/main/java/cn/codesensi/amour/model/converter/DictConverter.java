package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysDictData;
import cn.codesensi.amour.model.entity.SysDictType;
import cn.codesensi.amour.model.request.*;
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
 * 数据字典转换器 —— 字典类型/数据实体与 DTO、请求、响应对象间的映射
 * （MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code DictConverterImpl} 为 Spring 组件）；
 * 同名业务字段由 MapStruct 自动映射，仅审计字段与不可提交字段显式忽略。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DictConverter {

    /**
     * 将字典数据实体转换为字典项 DTO。
     *
     * @param dictData 字典数据实体
     * @return 字典项 DTO
     */
    DictDTO toDTO(SysDictData dictData);

    /**
     * 将字典数据实体列表转换为字典项 DTO 列表（逐元素复用 {@link #toDTO(SysDictData)} 的映射规则）。
     *
     * @param dictDataList 字典数据实体列表
     * @return 字典项 DTO 列表
     */
    List<DictDTO> toListDTO(List<SysDictData> dictDataList);

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
    DictDataPageDTO toPageDTO(DictDataPageRequest request);

    /**
     * DictInsertRequest → DictInsertDTO。
     *
     * @param request 新增字典条目请求
     * @return 新增字典条目 DTO
     */
    DictDataInsertDTO toInsertDTO(DictDataInsertRequest request);

    /**
     * DictUpdateRequest → DictUpdateDTO。
     *
     * @param request 修改字典条目请求
     * @return 修改字典条目 DTO
     */
    DictDataUpdateDTO toUpdateDTO(DictDataUpdateRequest request);

    /**
     * DictChangeStatusRequest → DictChangeStatusDTO。
     *
     * @param request 修改字典状态请求
     * @return 修改字典状态 DTO
     */
    DictChangeStatusDTO toChangeStatusDTO(DictChangeStatusRequest request);

    /**
     * DictTypeInsertRequest → DictTypeInsertDTO。
     *
     * @param request 新增字典类型请求
     * @return 新增字典类型 DTO
     */
    DictTypeInsertDTO toInsertDTO(DictTypeInsertRequest request);

    /**
     * DictTypeUpdateRequest → DictTypeUpdateDTO。
     *
     * @param request 修改字典类型请求
     * @return 修改字典类型 DTO
     */
    DictTypeUpdateDTO toUpdateDTO(DictTypeUpdateRequest request);

    /**
     * DictInsertDTO → SysDictData。
     * <p>
     * id 由雪花生成器填充，审计字段由实体监听器填充，builtin 走数据库默认值，均不参与映射。
     *
     * @param insertDTO 新增字典条目 DTO
     * @return 字典数据实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    @Mapping(target = "dictName", ignore = true)
    SysDictData toEntity(DictDataInsertDTO insertDTO);

    /**
     * DictUpdateDTO → SysDictData。
     * <p>
     * 审计字段由实体监听器维护；dictCode 创建后不可修改，status 由独立的状态接口维护；
     * builtin 与 dictName（回填展示字段）不在单条修改范围，均不参与映射。
     *
     * @param updateDTO 修改字典条目 DTO
     * @return 字典数据实体
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "dictCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    @Mapping(target = "dictName", ignore = true)
    SysDictData toEntity(DictDataUpdateDTO updateDTO);

    /**
     * DictTypeInsertDTO → SysDictType。
     * <p>
     * id 由雪花生成器填充，审计字段由实体监听器填充，builtin 走数据库默认值，均不参与映射。
     *
     * @param insertDTO 新增字典类型 DTO
     * @return 字典类型实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysDictType toEntity(DictTypeInsertDTO insertDTO);

    /**
     * DictTypeUpdateDTO → SysDictType。
     * <p>
     * 审计字段由实体监听器维护；dictCode 创建后不可修改，builtin 为内置标识，
     * 均不参与映射。
     *
     * @param updateDTO 修改字典类型 DTO
     * @return 字典类型实体
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "dictCode", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysDictType toEntity(DictTypeUpdateDTO updateDTO);

    /**
     * Page&lt;SysDictData&gt; → Page&lt;DictPageResponse&gt;
     * （records 逐元素复用实体 → 行响应对象的映射规则；dictName 为实体上的回填展示字段）。
     *
     * @param page 字典数据实体分页
     * @return 字典行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<DictPageResponse> toPageResponse(Page<SysDictData> page);

}
