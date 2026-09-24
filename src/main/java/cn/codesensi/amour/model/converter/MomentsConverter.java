package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.MomentsChangeStatusDTO;
import cn.codesensi.amour.model.dto.MomentsDTO;
import cn.codesensi.amour.model.dto.MomentsHistoryDTO;
import cn.codesensi.amour.model.dto.MomentsInsertDTO;
import cn.codesensi.amour.model.dto.MomentsPageDTO;
import cn.codesensi.amour.model.dto.MomentsUpdateDTO;
import cn.codesensi.amour.model.entity.PortalMoments;
import cn.codesensi.amour.model.request.MomentsChangeStatusRequest;
import cn.codesensi.amour.model.request.MomentsInsertRequest;
import cn.codesensi.amour.model.request.MomentsPageRequest;
import cn.codesensi.amour.model.request.MomentsUpdateRequest;
import cn.codesensi.amour.model.response.MomentsHistoryResponse;
import cn.codesensi.amour.model.response.MomentsPageResponse;
import cn.codesensi.amour.model.response.PortalMomentsResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 点点滴滴转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 字段与表列同名，除分页映射外均为自动映射；
 * 作者展示信息与审计用户名由服务层批量回填，实体无对应字段。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MomentsConverter {

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    MomentsPageDTO toPageDTO(MomentsPageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求
     * @return 新增参数 DTO
     */
    MomentsInsertDTO toInsertDTO(MomentsInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求
     * @return 修改参数 DTO
     */
    MomentsUpdateDTO toUpdateDTO(MomentsUpdateRequest request);

    /**
     * 新增参数 DTO → 实体（id 与审计字段由全局填充，显式忽略）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 点点滴滴实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalMoments toEntity(MomentsInsertDTO insertDTO);

    /**
     * 实体 → Service 出参 DTO（字段同名自动映射）；
     * 作者展示信息与 creatorName/updaterName 均由服务层批量回填，
     * 实体无对应字段，显式忽略以消除 Unmapped 警告。
     *
     * @param entity 点点滴滴实体
     * @return 点点滴滴 DTO
     */
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "qq", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "creatorName", ignore = true)
    @Mapping(target = "updaterName", ignore = true)
    MomentsDTO toDTO(PortalMoments entity);

    /**
     * Page&lt;PortalMoments&gt; → Page&lt;MomentsDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 点点滴滴实体分页
     * @return 点点滴滴 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<MomentsDTO> toPageDTO(Page<PortalMoments> page);

    /**
     * 条目 DTO → 管理端行响应（字段同名自动映射）。
     *
     * @param itemDTO 点点滴滴 DTO
     * @return 管理端行响应
     */
    MomentsPageResponse toResponse(MomentsDTO itemDTO);

    /**
     * Page&lt;MomentsDTO&gt; → Page&lt;MomentsPageResponse&gt;。
     *
     * @param page 点点滴滴 DTO 分页
     * @return 管理端行响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<MomentsPageResponse> toPageResponse(Page<MomentsDTO> page);

    /**
     * 条目 DTO → 门户响应（字段同名自动映射）。
     *
     * @param itemDTO 点点滴滴 DTO
     * @return 门户响应
     */
    PortalMomentsResponse toPortalResponse(MomentsDTO itemDTO);

    /**
     * Page&lt;MomentsDTO&gt; → Page&lt;PortalMomentsResponse&gt;。
     *
     * @param page 点点滴滴 DTO 分页
     * @return 门户响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<PortalMomentsResponse> toPortalPage(Page<MomentsDTO> page);

    /**
     * 历史分类/标签建议 DTO → 管理端响应（字段同名自动映射）。
     *
     * @param historyDTO 历史分类/标签建议 DTO
     * @return 管理端响应
     */
    MomentsHistoryResponse toResponse(MomentsHistoryDTO historyDTO);

    /**
     * 状态修改请求 → 状态修改参数 DTO。
     *
     * @param request 状态修改请求
     * @return 状态修改参数 DTO
     */
    MomentsChangeStatusDTO toChangeStatusDTO(MomentsChangeStatusRequest request);

}
