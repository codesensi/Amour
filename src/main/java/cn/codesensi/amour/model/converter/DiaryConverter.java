package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.DiaryDTO;
import cn.codesensi.amour.model.dto.DiaryInsertDTO;
import cn.codesensi.amour.model.dto.DiaryPageDTO;
import cn.codesensi.amour.model.dto.DiaryUpdateDTO;
import cn.codesensi.amour.model.entity.PortalDiary;
import cn.codesensi.amour.model.request.DiaryInsertRequest;
import cn.codesensi.amour.model.request.DiaryPageRequest;
import cn.codesensi.amour.model.request.DiaryUpdateRequest;
import cn.codesensi.amour.model.response.DiaryPageResponse;
import cn.codesensi.amour.model.response.PortalDiaryResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 情侣日记转换器（管理端与门户共用，MapStruct 编译期生成实现类）。
 * <p>
 * 字段与表列同名，除分页映射外均为自动映射；
 * 记录人展示信息与审计用户名由服务层批量回填，实体无对应字段。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DiaryConverter {

    /**
     * 分页查询请求 → 分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    DiaryPageDTO toPageDTO(DiaryPageRequest request);

    /**
     * 新增请求 → 新增参数 DTO。
     *
     * @param request 新增请求
     * @return 新增参数 DTO
     */
    DiaryInsertDTO toInsertDTO(DiaryInsertRequest request);

    /**
     * 修改请求 → 修改参数 DTO。
     *
     * @param request 修改请求
     * @return 修改参数 DTO
     */
    DiaryUpdateDTO toUpdateDTO(DiaryUpdateRequest request);

    /**
     * 新增参数 DTO → 实体（id 与审计字段由全局填充，显式忽略）。
     *
     * @param insertDTO 新增参数 DTO
     * @return 情侣日记实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalDiary toEntity(DiaryInsertDTO insertDTO);

    /**
     * 实体 → Service 出参 DTO（字段同名自动映射）；
     * nickname/avatar 与 creatorName/updaterName 均由服务层批量回填，
     * 实体无对应字段，显式忽略以消除 Unmapped 警告。
     *
     * @param entity 情侣日记实体
     * @return 情侣日记 DTO
     */
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "qq", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "creatorName", ignore = true)
    @Mapping(target = "updaterName", ignore = true)
    DiaryDTO toDTO(PortalDiary entity);

    /**
     * Page&lt;PortalDiary&gt; → Page&lt;DiaryDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 情侣日记实体分页
     * @return 情侣日记 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<DiaryDTO> toPageDTO(Page<PortalDiary> page);

    /**
     * 条目 DTO → 管理端行响应（字段同名自动映射）。
     *
     * @param itemDTO 情侣日记 DTO
     * @return 管理端行响应
     */
    DiaryPageResponse toResponse(DiaryDTO itemDTO);

    /**
     * Page&lt;DiaryDTO&gt; → Page&lt;DiaryPageResponse&gt;。
     *
     * @param page 情侣日记 DTO 分页
     * @return 管理端行响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<DiaryPageResponse> toPageResponse(Page<DiaryDTO> page);

    /**
     * 条目 DTO → 门户响应（字段同名自动映射）。
     *
     * @param itemDTO 情侣日记 DTO
     * @return 门户响应
     */
    PortalDiaryResponse toPortalResponse(DiaryDTO itemDTO);

    /**
     * Page&lt;DiaryDTO&gt; → Page&lt;PortalDiaryResponse&gt;。
     *
     * @param page 情侣日记 DTO 分页
     * @return 门户响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<PortalDiaryResponse> toPortalPage(Page<DiaryDTO> page);

}
