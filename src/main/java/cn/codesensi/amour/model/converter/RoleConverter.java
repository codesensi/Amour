package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleSaveDTO;
import cn.codesensi.amour.model.request.AssignMenusRequest;
import cn.codesensi.amour.model.request.RoleSaveRequest;
import org.mapstruct.Mapper;

/**
 * 角色相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * RoleSaveRequest → RoleSaveDTO
     */
    RoleSaveDTO toSaveDTO(RoleSaveRequest request);

    /**
     * AssignMenusRequest → AssignMenusDTO
     */
    AssignMenusDTO toAssignMenusDTO(AssignMenusRequest request);

}
