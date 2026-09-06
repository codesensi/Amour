package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.request.AssignRolesRequest;
import cn.codesensi.amour.model.request.UserPageRequest;
import cn.codesensi.amour.model.request.UserSaveRequest;
import cn.codesensi.amour.model.response.MenuResponse;
import cn.codesensi.amour.model.response.UserInfoResponse;
import cn.codesensi.amour.model.response.UserPageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用户相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * UserSaveRequest → UserSaveDTO
     */
    UserSaveDTO toSaveDTO(UserSaveRequest request);

    /**
     * UserInfoDTO → UserInfoResponse
     */
    UserInfoResponse toInfoResponse(UserInfoDTO userInfoDTO);

    /**
     * MenuDTO → MenuResponse
     */
    MenuResponse mapMenuResponse(MenuDTO menuDTO);

    /**
     * AssignRolesRequest → AssignRolesDTO
     */
    AssignRolesDTO toAssignRolesDTO(AssignRolesRequest request);

    /**
     * UserPageRequest → UserPageDTO
     */
    UserPageDTO toPageDTO(UserPageRequest request);

    /**
     * Page<SysUser> → Page<UserPageResponse>
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<UserPageResponse> toPageResponse(Page<SysUser> page);

    /**
     * UserSaveDTO → SysUser
     */
    SysUser toEntity(UserSaveDTO userSaveDTO);

    /**
     * SysUser → UserInfoDTO
     */
    UserInfoDTO toInfoDTO(SysUser sysUser);

    /**
     * SysMenu → MenuDTO
     */
    MenuDTO toMenuDTO(SysMenu sysMenu);

    /**
     * List<SysMenu> → List<MenuDTO>
     */
    List<MenuDTO> toMenuDTOList(List<SysMenu> sysMenus);

}
