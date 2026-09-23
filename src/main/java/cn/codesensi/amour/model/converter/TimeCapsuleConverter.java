package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalTimeCapsule;
import cn.codesensi.amour.model.request.TimeCapsuleChangeHiddenRequest;
import cn.codesensi.amour.model.request.TimeCapsuleInsertRequest;
import cn.codesensi.amour.model.request.TimeCapsulePageRequest;
import cn.codesensi.amour.model.request.TimeCapsuleUpdateRequest;
import cn.codesensi.amour.model.response.PortalTimeCapsuleResponse;
import cn.codesensi.amour.model.response.TimeCapsulePageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 时间胶囊转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 管理端字段与表列同名，除分页映射外均为自动映射；门户响应增加
 * {@code unlocked} 解锁标识（由服务层按当前时间判定后下发）。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeCapsuleConverter {

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    TimeCapsulePageDTO toPageDTO(TimeCapsulePageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求
     * @return 新增参数 DTO
     */
    TimeCapsuleInsertDTO toInsertDTO(TimeCapsuleInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求
     * @return 修改参数 DTO
     */
    TimeCapsuleUpdateDTO toUpdateDTO(TimeCapsuleUpdateRequest request);

    /**
     * 显隐修改请求 → 显隐修改参数 DTO。
     *
     * @param request 显隐修改请求
     * @return 显隐修改参数 DTO
     */
    TimeCapsuleChangeHiddenDTO toChangeHiddenDTO(TimeCapsuleChangeHiddenRequest request);

    /**
     * 新增参数 DTO → 实体（id 与审计字段由全局填充，显式忽略）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 时间胶囊实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalTimeCapsule toEntity(TimeCapsuleInsertDTO insertDTO);

    /**
     * 实体 → Service 出参 DTO（字段同名自动映射）；
     * creatorName/updaterName 由服务层批量回填、unlocked 由服务层按当前时间判定，
     * 实体均无对应字段，显式忽略以消除 Unmapped 警告。
     *
     * @param entity 时间胶囊实体
     * @return 时间胶囊 DTO
     */
    @Mapping(target = "creatorName", ignore = true)
    @Mapping(target = "updaterName", ignore = true)
    @Mapping(target = "unlocked", ignore = true)
    TimeCapsuleDTO toDTO(PortalTimeCapsule entity);

    /**
     * Page&lt;PortalTimeCapsule&gt; → Page&lt;TimeCapsuleItemDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 时间胶囊实体分页
     * @return 时间胶囊 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<TimeCapsuleDTO> toPageDTO(Page<PortalTimeCapsule> page);

    /**
     * 条目 DTO → 管理端行响应（字段同名自动映射）。
     *
     * @param itemDTO 时间胶囊 DTO
     * @return 管理端行响应
     */
    TimeCapsulePageResponse toResponse(TimeCapsuleDTO itemDTO);

    /**
     * Page&lt;TimeCapsuleItemDTO&gt; → Page&lt;TimeCapsulePageResponse&gt;。
     *
     * @param page 时间胶囊 DTO 分页
     * @return 管理端行响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<TimeCapsulePageResponse> toPageResponse(Page<TimeCapsuleDTO> page);

    /**
     * 条目 DTO → 门户响应（字段同名自动映射）。
     *
     * @param itemDTO 时间胶囊 DTO
     * @return 门户响应
     */
    PortalTimeCapsuleResponse toPortalResponse(TimeCapsuleDTO itemDTO);

    /**
     * Page&lt;TimeCapsuleItemDTO&gt; → Page&lt;PortalTimeCapsuleResponse&gt;。
     *
     * @param page 时间胶囊 DTO 分页
     * @return 门户响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<PortalTimeCapsuleResponse> toPortalPage(Page<TimeCapsuleDTO> page);

}
