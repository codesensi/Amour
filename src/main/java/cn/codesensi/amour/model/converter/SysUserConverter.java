package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.MenuDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserSaveDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysUser;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 用户相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface SysUserConverter {

    /**
     * SysUser → UserInfoDTO
     */
    UserInfoDTO toUserInfoDTO(SysUser sysUser);

    /**
     * SysMenu → MenuDTO
     */
    MenuDTO toMenuDTO(SysMenu sysMenu);

    /**
     * List<SysMenu> → List<MenuDTO>
     */
    List<MenuDTO> toMenuDTOList(List<SysMenu> sysMenus);

    /**
     * UserSaveDTO → SysUser
     */
    SysUser toEntity(UserSaveDTO userSaveDTO);

}
