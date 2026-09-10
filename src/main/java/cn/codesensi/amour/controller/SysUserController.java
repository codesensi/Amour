package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.UserConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.UserInfoResponse;
import cn.codesensi.amour.model.response.UserPageResponse;
import cn.codesensi.amour.service.SysUserService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户信息表 控制层。
 *
 * @author codesensi
 * @since 1.0
 */
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final SysUserService sysUserService;
    private final UserConverter userConverter;

    /**
     * 分页查询用户信息表。
     * <p>
     * 用户名称、手机号为模糊匹配，状态为精确匹配，条件缺省时自动忽略。
     *
     * @param userPageRequest 分页查询参数
     * @return 分页对象
     */
    @SaCheckPermission("system:user:page")
    @GetMapping("/page")
    public Page<UserPageResponse> page(@Valid UserPageRequest userPageRequest) {
        UserPageDTO userPageDTO = userConverter.toPageDTO(userPageRequest);
        Page<SysUser> sysUserPage = sysUserService.page(userPageDTO);
        return userConverter.toPageResponse(sysUserPage);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/current-user")
    public UserInfoResponse currentUser() {
        UserInfoDTO userInfoDTO = sysUserService.getCurrentUser();
        return userConverter.toInfoResponse(userInfoDTO);
    }

    /**
     * 更新当前登录用户资料
     * <p>
     * 登录即可操作，仅允许修改自己的白名单资料字段。
     *
     * @param request 资料更新请求参数
     */
    @PutMapping("/update-profile")
    public void updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileUpdateDTO userProfileUpdateDTO = userConverter.toProfileUpdateDTO(request);
        sysUserService.updateProfile(userProfileUpdateDTO);
    }

    /**
     * 修改当前登录用户名
     * <p>
     * 用户名为登录凭证，修改成功后服务端踢出会话，需使用新用户名重新登录。
     *
     * @param request 改名请求参数
     */
    @PutMapping("/rename")
    public void rename(@Valid @RequestBody UserRenameRequest request) {
        sysUserService.rename(request.getUsername());
    }

    /**
     * 修改当前登录用户密码
     * <p>
     * 校验原密码匹配，修改成功后服务端踢出会话，需使用新密码重新登录。
     *
     * @param request 密码修改请求参数
     */
    @PutMapping("/update-password")
    public void updatePassword(@Valid @RequestBody UserPasswordUpdateRequest request) {
        UserPasswordUpdateDTO userPasswordUpdateDTO = userConverter.toPasswordUpdateDTO(request);
        sysUserService.updatePassword(userPasswordUpdateDTO);
    }

    /**
     * 新增用户信息
     *
     * @param request 新增用户请求参数
     */
    @SaCheckPermission("system:user:insert")
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody UserInsertRequest request) {
        UserInsertDTO userInsertDTO = userConverter.toInsertDTO(request);
        sysUserService.insert(userInsertDTO);
    }

    /**
     * 修改用户信息
     *
     * @param request 修改用户请求参数
     */
    @SaCheckPermission("system:user:update")
    @PutMapping("/update")
    public void update(@Valid @RequestBody UserUpdateRequest request) {
        UserUpdateDTO userUpdateDTO = userConverter.toUpdateDTO(request);
        sysUserService.update(userUpdateDTO);
    }

    /**
     * 修改用户状态
     *
     * @param request 修改状态请求参数
     */
    @SaCheckPermission("system:user:update")
    @PutMapping("/change-status")
    public void changeStatus(@Valid @RequestBody UserChangeStatusRequest request) {
        UserChangeStatusDTO userChangeStatusDTO = userConverter.toChangeStatusDTO(request);
        sysUserService.changeStatus(userChangeStatusDTO);
    }

    /**
     * 删除用户信息(支持单个或批量，ID 以英文逗号分隔)
     *
     * @param ids 用户ID列表
     */
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        sysUserService.delete(List.of(ids));
    }

    /**
     * 重置用户密码为系统默认密码
     *
     * @param id 用户ID
     */
    @SaCheckPermission("system:user:update")
    @PutMapping("/reset-password/{id}")
    public void resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
    }

    /**
     * 配置用户角色
     *
     * @param request 配置用户角色请求参数
     */
    @SaCheckPermission("system:user:update")
    @PutMapping("/assign-roles")
    public void assignRoles(@Valid @RequestBody AssignRolesRequest request) {
        AssignRolesDTO assignRolesDTO = userConverter.toAssignRolesDTO(request);
        sysUserService.assignRoles(assignRolesDTO);
    }

    /**
     * 查询用户已分配的角色ID列表（分配角色弹窗预勾选用）。
     * <p>
     * id 以字符串形式下发，与 RoleResponse 及 el-select 的选项值类型对齐。
     *
     * @param id 用户ID
     * @return 角色ID列表
     */
    @SaCheckPermission("system:user:update")
    @GetMapping("/role-ids/{id}")
    public List<String> roleIds(@PathVariable Long id) {
        return sysUserService.listRoleIdsByUserId(id)
                .stream().map(String::valueOf).toList();
    }

}