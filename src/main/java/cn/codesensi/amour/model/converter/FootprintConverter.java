package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalFootprint;
import cn.codesensi.amour.model.request.FootprintChangeHiddenRequest;
import cn.codesensi.amour.model.request.FootprintInsertRequest;
import cn.codesensi.amour.model.request.FootprintPageRequest;
import cn.codesensi.amour.model.request.FootprintUpdateRequest;
import cn.codesensi.amour.model.response.FootprintPageResponse;
import cn.codesensi.amour.model.response.FootprintResponse;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 足迹地图转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 日期在请求层为 yyyy-MM-dd 字符串、实体层为 LocalDate、行数据与响应层为
 * 同格式字符串，由 {@code yyyy-MM-dd} 格式转换承接；照片地址（photoUrl）
 * 入库即为目标形态（站内 /file/view/{id} 或外链），各层同名直传。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FootprintConverter {

    /** 到访日期格式（与 arrival_date DATE 列及前端契约一致） */
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    @Mapping(target = "arrivalDateBegin", source = "arrivalDateBegin", qualifiedByName = "stringToDate")
    @Mapping(target = "arrivalDateEnd", source = "arrivalDateEnd", qualifiedByName = "stringToDate")
    FootprintPageDTO toPageDTO(FootprintPageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求
     * @return 新增参数 DTO
     */
    @Mapping(target = "arrivalDate", source = "arrivalDate", qualifiedByName = "stringToDate")
    FootprintInsertDTO toInsertDTO(FootprintInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求
     * @return 修改参数 DTO
     */
    @Mapping(target = "arrivalDate", source = "arrivalDate", qualifiedByName = "stringToDate")
    FootprintUpdateDTO toUpdateDTO(FootprintUpdateRequest request);

    /**
     * 修改显隐请求 → 修改显隐业务数据。
     *
     * @param request 修改显隐请求
     * @return 修改显隐业务数据
     */
    FootprintChangeHiddenDTO toChangeHiddenDTO(FootprintChangeHiddenRequest request);

    /**
     * 新增参数 DTO → 实体（id 由雪花生成器填充；
     * 审计字段由基类监听器与全局逻辑删除配置承接）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 足迹地图实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalFootprint toEntity(FootprintInsertDTO insertDTO);

    /**
     * 实体 → Service 出参条目 DTO（字段同名自动映射，photoUrl 原值直传）。
     *
     * @param entity 足迹地图实体
     * @return 足迹条目 DTO
     */
    @Mapping(target = "arrivalDate", dateFormat = "yyyy-MM-dd")
    FootprintDTO toDTO(PortalFootprint entity);

    /**
     * Page&lt;PortalFootprint&gt; → Page&lt;FootprintItemDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 足迹实体分页
     * @return 足迹条目 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<FootprintDTO> toPageDTO(Page<PortalFootprint> page);

    /**
     * 条目 DTO → 管理端分页行响应。
     *
     * @param itemDTO 足迹条目 DTO
     * @return 管理端行响应对象
     */
    FootprintPageResponse toResponse(FootprintDTO itemDTO);

    /**
     * Page&lt;FootprintItemDTO&gt; → Page&lt;FootprintPageResponse&gt;
     * （records 逐元素复用条目 DTO → 行响应对象的映射规则）。
     *
     * @param page 足迹条目 DTO 分页
     * @return 管理端行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<FootprintPageResponse> toPageResponse(Page<FootprintDTO> page);

    /**
     * 条目 DTO → 门户足迹响应（字段同名自动映射）。
     *
     * @param itemDTO 足迹条目 DTO
     * @return 门户足迹响应对象
     */
    FootprintResponse toPortalResponse(FootprintDTO itemDTO);

    /**
     * 条目 DTO 列表 → 门户足迹响应列表
     * （逐元素复用 {@link #toPortalResponse(FootprintDTO)} 的映射规则）。
     *
     * @param items 足迹条目 DTO 列表
     * @return 门户足迹响应列表
     */
    List<FootprintResponse> toPortalResponseList(List<FootprintDTO> items);

    /**
     * Page&lt;FootprintItemDTO&gt; → Page&lt;FootprintResponse&gt;
     * （records 逐元素复用 {@link #toPortalResponse(FootprintDTO)} 的映射规则）。
     *
     * @param page 足迹条目 DTO 分页
     * @return 门户足迹响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<FootprintResponse> toPortalPage(Page<FootprintDTO> page);

    /**
     * yyyy-MM-dd 字符串 → 日期（空值安全;分页范围/入库日期的统一转换口）。
     *
     * @param value 日期字符串
     * @return 日期;空白返回 null
     */
    @Named("stringToDate")
    default LocalDate parseArrivalDate(String value) {
        return StrUtil.isBlank(value) ? null : LocalDate.parse(value, DATE_FORMATTER);
    }

}
