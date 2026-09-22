package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalLoveList;
import cn.codesensi.amour.model.request.LoveListChangeHiddenRequest;
import cn.codesensi.amour.model.request.LoveListInsertRequest;
import cn.codesensi.amour.model.request.LoveListPageRequest;
import cn.codesensi.amour.model.request.LoveListUpdateRequest;
import cn.codesensi.amour.model.response.LoveListPageResponse;
import cn.codesensi.amour.model.response.PortalLoveListResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 恋爱清单转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 管理端字段与表列同名，除分页映射外均为自动映射；门户响应字段对齐前端契约，
 * 由 {@code content→text}、{@code photo→img} 的重命名映射完成，
 * 完成状态经 {@link #toBoolean(Integer)} 转为布尔。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoveListConverter {

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    LoveListPageDTO toPageDTO(LoveListPageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求
     * @return 新增参数 DTO
     */
    LoveListInsertDTO toInsertDTO(LoveListInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求
     * @return 修改参数 DTO
     */
    LoveListUpdateDTO toUpdateDTO(LoveListUpdateRequest request);

    /**
     * 显隐修改请求 → 显隐修改参数 DTO。
     *
     * @param request 显隐修改请求
     * @return 显隐修改参数 DTO
     */
    LoveListChangeHiddenDTO toChangeHiddenDTO(LoveListChangeHiddenRequest request);

    /**
     * 新增参数 DTO → 实体（id 由雪花生成器填充；
     * 审计字段由基类监听器与全局逻辑删除配置承接）。
     * 修改不再经实体组装（Service 层使用 UpdateChain 显式逐列更新）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 恋爱清单项实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalLoveList toEntity(LoveListInsertDTO insertDTO);

    /**
     * 实体 → Service 出参 DTO（字段同名自动映射）；
     * creatorName/updaterName 由服务层批量回填，实体无对应字段，显式忽略以消除 Unmapped 警告。
     *
     * @param entity 恋爱清单项实体
     * @return 清单项 DTO
     */
    @Mapping(target = "creatorName", ignore = true)
    @Mapping(target = "updaterName", ignore = true)
    LoveListItemDTO toDTO(PortalLoveList entity);

    /**
     * Page&lt;PortalLoveList&gt; → Page&lt;LoveListItemDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 清单项实体分页
     * @return 清单项 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<LoveListItemDTO> toPageDTO(Page<PortalLoveList> page);

    /**
     * 条目 DTO → 管理端分页行响应。
     *
     * @param itemDTO 清单项 DTO
     * @return 管理端行响应对象
     */
    LoveListPageResponse toResponse(LoveListItemDTO itemDTO);

    /**
     * Page&lt;LoveListItemDTO&gt; → Page&lt;LoveListPageResponse&gt;
     * （records 逐元素复用条目 DTO → 行响应对象的映射规则）。
     *
     * @param page 清单项 DTO 分页
     * @return 管理端行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<LoveListPageResponse> toPageResponse(Page<LoveListItemDTO> page);

    /**
     * 条目 DTO → 门户清单响应。
     *
     * @param itemDTO 清单项 DTO
     * @return 门户清单响应对象
     */
    @Mapping(source = "content", target = "text")
    @Mapping(source = "photo", target = "img")
    PortalLoveListResponse toPortalResponse(LoveListItemDTO itemDTO);

    /**
     * Page&lt;LoveListItemDTO&gt; → Page&lt;PortalLoveListResponse&gt;
     * （records 逐元素复用 {@link #toPortalResponse(LoveListItemDTO)} 的映射规则）。
     *
     * @param page 清单项 DTO 分页
     * @return 门户清单响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<PortalLoveListResponse> toPortalPage(Page<LoveListItemDTO> page);

    /**
     * 完成状态数值 → 布尔（0-未完成 → false，1-已完成 → true）。
     *
     * @param done 完成状态存储值
     * @return 门户契约的布尔形态；null 视为未完成
     */
    default Boolean toBoolean(Integer done) {
        return done != null && done != 0;
    }

}
