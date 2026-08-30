package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.AssignRolesDTO;
import cn.codesensi.amour.model.dto.MenuDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserSaveDTO;
import cn.codesensi.amour.model.request.AssignRolesRequest;
import cn.codesensi.amour.model.request.UserSaveRequest;
import cn.codesensi.amour.model.response.MenuResponse;
import cn.codesensi.amour.model.response.UserInfoResponse;
import org.mapstruct.Mapper;

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

}
