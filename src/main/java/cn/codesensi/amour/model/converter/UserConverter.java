package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.UserInfoResponse;
import cn.codesensi.amour.model.response.UserPageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户相关对象转换
 *
 * @author codesensi
 * @since 1.0
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
     * UserProfileUpdateRequest → UserProfileUpdateDTO
     */
    UserProfileUpdateDTO toProfileUpdateDTO(UserProfileUpdateRequest request);

    /**
     * UserPasswordUpdateRequest → UserPasswordUpdateDTO
     */
    UserPasswordUpdateDTO toPasswordUpdateDTO(UserPasswordUpdateRequest request);

    /**
     * Page<SysUser> → Page<UserPageResponse>
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<UserPageResponse> toPageResponse(Page<SysUser> page);

    /**
     * UserInsertDTO → SysUser
     * <p>
     * id 由雪花生成器填充，审计字段由实体监听器填充，builtin 走数据库默认值；
     * password 由服务层 BCrypt 加密后设置，均不参与映射。
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysUser toEntity(UserInsertDTO userInsertDTO);

    /**
     * UserUpdateDTO → SysUser(仅资料字段，id 用于定位更新；
     * 用户名/密码/状态/身份证/手机号不在映射范围，审计字段由实体监听器维护)
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysUser toEntity(UserUpdateDTO userUpdateDTO);

    /**
     * UserProfileUpdateDTO → SysUser(仅资料字段，id 由调用方回填；
     * 用户名/密码/状态/身份证/手机号不在映射范围，审计字段由实体监听器维护)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "idCard", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysUser toEntity(UserProfileUpdateDTO userProfileUpdateDTO);

    /**
     * SysUser → UserInfoDTO
     * <p>
     * roles/perms/menus 为聚合字段，由调用方在转换后装配，不参与映射。
     */
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "perms", ignore = true)
    @Mapping(target = "menus", ignore = true)
    UserInfoDTO toInfoDTO(SysUser sysUser);

    /**
     * UserInfoDTO 浅拷贝（缓存命中后的防御性复制，避免写后突变缓存中的共享实例）
     */
    UserInfoDTO copy(UserInfoDTO source);

}