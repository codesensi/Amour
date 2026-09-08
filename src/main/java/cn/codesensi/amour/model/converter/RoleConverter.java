package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.RolePageResponse;
import cn.codesensi.amour.model.response.RoleResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 角色相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * RoleInsertRequest → RoleInsertDTO
     */
    RoleInsertDTO toInsertDTO(RoleInsertRequest request);

    /**
     * RoleUpdateRequest → RoleUpdateDTO
     */
    RoleUpdateDTO toUpdateDTO(RoleUpdateRequest request);

    /**
     * RoleChangeStatusRequest → RoleChangeStatusDTO
     */
    RoleChangeStatusDTO toChangeStatusDTO(RoleChangeStatusRequest request);

    /**
     * AssignMenusRequest → AssignMenusDTO
     */
    AssignMenusDTO toAssignMenusDTO(AssignMenusRequest request);

    /**
     * RoleInsertDTO → SysRole
     */
    SysRole toEntity(RoleInsertDTO roleInsertDTO);

    /**
     * RoleUpdateDTO → SysRole
     */
    SysRole toEntity(RoleUpdateDTO roleUpdateDTO);

    /**
     * RolePageRequest → RolePageDTO
     */
    RolePageDTO toPageDTO(RolePageRequest request);

    /**
     * Page<SysRole> → Page<RolePageResponse>
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<RolePageResponse> toPageResponse(Page<SysRole> page);

    /**
     * List<SysRole> → List<RoleResponse>
     */
    List<RoleResponse> toListResponse(List<SysRole> list);

}
