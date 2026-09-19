package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalLovePhoto;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.LovePhotoPageResponse;
import cn.codesensi.amour.model.response.LovePhotoResponse;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 恋爱相册照片转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 管理端字段与表列同名，除分页映射外均为自动映射；门户响应字段对齐前端契约，
 * 由 {@code url→img}、{@code caption→text}、{@code dateText→date}、
 * {@code tags→tags[]} 的重命名映射完成。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LovePhotoConverter {

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    LovePhotoPageDTO toPageDTO(LovePhotoPageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求
     * @return 新增参数 DTO
     */
    LovePhotoInsertDTO toInsertDTO(LovePhotoInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求
     * @return 修改参数 DTO
     */
    LovePhotoUpdateDTO toUpdateDTO(LovePhotoUpdateRequest request);

    /**
     * 显隐修改请求 → 显隐修改参数 DTO。
     *
     * @param request 显隐修改请求
     * @return 显隐修改参数 DTO
     */
    LovePhotoChangeHiddenDTO toChangeHiddenDTO(LovePhotoChangeHiddenRequest request);

    /**
     * 新增参数 DTO → 实体（id 由雪花生成器填充，标签集合由 Service 规范化后显式写入；
     * 审计字段由基类监听器与全局逻辑删除配置承接）。
     * 修改不再经实体组装（Service 层使用 UpdateChain 显式逐列更新）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 恋爱相册照片实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalLovePhoto toEntity(LovePhotoInsertDTO insertDTO);

    /**
     * 实体 → Service 出参 DTO（字段同名自动映射）。
     *
     * @param entity 恋爱相册照片实体
     * @return 照片条目 DTO
     */
    LovePhotoDTO toDTO(PortalLovePhoto entity);

    /**
     * Page&lt;PortalLovePhoto&gt; → Page&lt;LovePhotoDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 照片实体分页
     * @return 照片条目 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<LovePhotoDTO> toPageDTO(Page<PortalLovePhoto> page);

    /**
     * 条目 DTO → 管理端分页行响应。
     *
     * @param itemDTO 照片条目 DTO
     * @return 管理端行响应对象
     */
    LovePhotoPageResponse toResponse(LovePhotoDTO itemDTO);

    /**
     * Page&lt;LovePhotoDTO&gt; → Page&lt;LovePhotoPageResponse&gt;
     * （records 逐元素复用条目 DTO → 行响应对象的映射规则）。
     *
     * @param page 照片条目 DTO 分页
     * @return 管理端行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<LovePhotoPageResponse> toPageResponse(Page<LovePhotoDTO> page);

    /**
     * 条目 DTO → 门户照片响应。
     *
     * @param itemDTO 照片条目 DTO
     * @return 门户照片响应对象
     */
    @Mapping(source = "url", target = "img")
    @Mapping(source = "caption", target = "text")
    @Mapping(source = "dateText", target = "date")
    LovePhotoResponse toPortalResponse(LovePhotoDTO itemDTO);

    /**
     * Page&lt;LovePhotoDTO&gt; → Page&lt;LovePhotoResponse&gt;
     * （records 逐元素复用 {@link #toPortalResponse(LovePhotoDTO)} 的映射规则）。
     *
     * @param page 照片条目 DTO 分页
     * @return 门户照片响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<LovePhotoResponse> toPortalPage(Page<LovePhotoDTO> page);

    /**
     * 逗号分隔标签串 → 标签集合（null 安全；空白项剔除）。
     *
     * @param tags 逗号分隔标签串
     * @return 标签集合；无标签时空数组
     */
    default List<String> splitTags(String tags) {
        return StrUtil.isBlank(tags) ? List.of() : StrUtil.splitTrim(tags, ",");
    }

}
