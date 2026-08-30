package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.RoleSaveDTO;
import cn.codesensi.amour.model.entity.SysRole;
import org.mapstruct.Mapper;

/**
 * 角色相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface SysRoleConverter {

    /**
     * RoleSaveDTO → SysRole
     */
    SysRole toEntity(RoleSaveDTO roleSaveDTO);

}
