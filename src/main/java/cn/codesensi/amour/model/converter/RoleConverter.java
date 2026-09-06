package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleInsertDTO;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.request.AssignMenusRequest;
import cn.codesensi.amour.model.request.RoleInsertRequest;
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
     * RoleInsertRequest → RoleInsertDTO
     */
    RoleInsertDTO toInsertDTO(RoleInsertRequest request);

    /**
     * AssignMenusRequest → AssignMenusDTO
     */
    AssignMenusDTO toAssignMenusDTO(AssignMenusRequest request);

    /**
     * RoleInsertDTO → SysRole
     */
    SysRole toEntity(RoleInsertDTO roleInsertDTO);

}
