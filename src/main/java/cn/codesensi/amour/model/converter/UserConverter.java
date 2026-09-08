package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.request.AssignRolesRequest;
import cn.codesensi.amour.model.request.UserChangeStatusRequest;
import cn.codesensi.amour.model.request.UserPageRequest;
import cn.codesensi.amour.model.request.UserInsertRequest;
import cn.codesensi.amour.model.request.UserUpdateRequest;
import cn.codesensi.amour.model.response.UserInfoResponse;
import cn.codesensi.amour.model.response.UserPageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring", uses = MenuConverter.class)
public interface UserConverter {

    /**
     * UserInsertRequest → UserInsertDTO
     */
    UserInsertDTO toInsertDTO(UserInsertRequest request);

    /**
     * UserUpdateRequest → UserUpdateDTO
     */
    UserUpdateDTO toUpdateDTO(UserUpdateRequest request);

    /**
     * UserChangeStatusRequest → UserChangeStatusDTO
     */
    UserChangeStatusDTO toChangeStatusDTO(UserChangeStatusRequest request);

    /**
     * UserInfoDTO → UserInfoResponse
     */
    UserInfoResponse toInfoResponse(UserInfoDTO userInfoDTO);

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
     * UserInsertDTO → SysUser
     */
    SysUser toEntity(UserInsertDTO userInsertDTO);

    /**
     * UserUpdateDTO → SysUser(仅资料字段,id 用于定位更新;用户名/密码/状态不在映射范围)
     */
    SysUser toEntity(UserUpdateDTO userUpdateDTO);

    /**
     * SysUser → UserInfoDTO
     */
    UserInfoDTO toInfoDTO(SysUser sysUser);

}
