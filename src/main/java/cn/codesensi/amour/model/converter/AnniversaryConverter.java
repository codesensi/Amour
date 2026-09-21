package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.AnniversaryChangeHiddenDTO;
import cn.codesensi.amour.model.dto.AnniversaryDTO;
import cn.codesensi.amour.model.dto.AnniversaryInsertDTO;
import cn.codesensi.amour.model.dto.AnniversaryPageDTO;
import cn.codesensi.amour.model.dto.AnniversaryUpdateDTO;
import cn.codesensi.amour.model.entity.PortalAnniversary;
import cn.codesensi.amour.model.request.AnniversaryChangeHiddenRequest;
import cn.codesensi.amour.model.request.AnniversaryInsertRequest;
import cn.codesensi.amour.model.request.AnniversaryPageRequest;
import cn.codesensi.amour.model.request.AnniversaryUpdateRequest;
import cn.codesensi.amour.model.response.AnniversaryPageResponse;
import cn.codesensi.amour.model.response.PortalAnniversaryResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 纪念日转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 纪念日日期在请求层为 yyyy-MM-dd 字符串、实体层为 LocalDate、响应层为
 * 同格式字符串，由 {@code yyyy-MM-dd} 格式转换承接。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AnniversaryConverter {

    /** 纪念日日期格式（与 anniversary_date DATE 列及前端契约一致） */
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    AnniversaryPageDTO toPageDTO(AnniversaryPageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求参数
     * @return 新增参数 DTO
     */
    AnniversaryInsertDTO toInsertDTO(AnniversaryInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求参数
     * @return 修改参数 DTO
     */
    AnniversaryUpdateDTO toUpdateDTO(AnniversaryUpdateRequest request);

    /**
     * 显隐修改请求 → 显隐修改参数 DTO。
     *
     * @param request 显隐修改请求参数
     * @return 显隐修改参数 DTO
     */
    AnniversaryChangeHiddenDTO toChangeHiddenDTO(AnniversaryChangeHiddenRequest request);

    /**
     * 新增参数 DTO → 实体（id 由雪花生成器填充；
     * 审计字段由基类监听器与全局逻辑删除配置承接）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 纪念日实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalAnniversary toEntity(AnniversaryInsertDTO insertDTO);

    /**
     * 修改参数 DTO → 实体（id 同名直传；hidden 走独立 change-hidden 端点；
     * 审计字段由基类监听器与全局逻辑删除配置承接）。
     *
     * @param updateDTO 修改参数 DTO
     * @return 纪念日实体
     */
    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalAnniversary toEntity(AnniversaryUpdateDTO updateDTO);

    /**
     * 实体 → 条目 DTO（字段同名自动映射）。
     *
     * @param entity 纪念日实体
     * @return 纪念日条目 DTO
     */
    AnniversaryDTO toDTO(PortalAnniversary entity);

    /**
     * Page&lt;PortalAnniversary&gt; → Page&lt;AnniversaryDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 纪念日实体分页
     * @return 纪念日条目 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<AnniversaryDTO> toPageDTO(Page<PortalAnniversary> page);

    /**
     * 条目 DTO → 门户纪念日响应（字段同名自动映射）。
     *
     * @param dto 纪念日条目 DTO
     * @return 门户纪念日响应对象
     */
    PortalAnniversaryResponse toResponse(AnniversaryDTO dto);

    /**
     * Page&lt;AnniversaryDTO&gt; → Page&lt;AnniversaryPageResponse&gt;（管理端分页，完整字段；
     * records 逐元素复用条目 DTO → 响应对象的映射规则）。
     *
     * @param page 纪念日条目 DTO 分页
     * @return 纪念日行响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<AnniversaryPageResponse> toPageResponse(Page<AnniversaryDTO> page);

    /**
     * Page&lt;AnniversaryDTO&gt; → Page&lt;PortalAnniversaryResponse&gt;（门户分页；
     * records 逐元素复用条目 DTO → 响应对象的映射规则）。
     *
     * @param page 纪念日条目 DTO 分页
     * @return 门户纪念日响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<PortalAnniversaryResponse> toPortalPage(Page<AnniversaryDTO> page);

    /**
     * yyyy-MM-dd 字符串 → 日期（空值安全;入库日期的统一转换口）。
     *
     * @param value 日期字符串
     * @return 日期;空白返回 null
     */
    @Named("stringToDate")
    default LocalDate parseAnniversaryDate(String value) {
        return cn.hutool.core.util.StrUtil.isBlank(value) ? null : LocalDate.parse(value, DATE_FORMATTER);
    }

}
