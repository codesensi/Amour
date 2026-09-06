package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.UserConverter;
import cn.codesensi.amour.model.dto.AssignRolesDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserInsertDTO;
import cn.codesensi.amour.model.dto.UserPageDTO;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.request.AssignRolesRequest;
import cn.codesensi.amour.model.request.UserInsertRequest;
import cn.codesensi.amour.model.request.UserPageRequest;
import cn.codesensi.amour.model.response.UserInfoResponse;
import cn.codesensi.amour.model.response.UserPageResponse;
import cn.codesensi.amour.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 用户信息表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
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
     * 用户名称、手机号为模糊匹配,状态为精确匹配,条件缺省时自动忽略。
     *
     * @param userPageRequest 分页查询参数
     * @return 分页对象
     */
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
        long userId = StpUtil.getLoginIdAsLong();
        UserInfoDTO userInfoDTO = sysUserService.getCurrentUser(userId);
        return userConverter.toInfoResponse(userInfoDTO);
    }

    /**
     * 新增用户信息
     *
     * @param request 新增用户请求参数
     */
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody UserInsertRequest request) {
        UserInsertDTO userInsertDTO = userConverter.toInsertDTO(request);
        sysUserService.insert(userInsertDTO);
    }

    /**
     * 配置用户角色
     *
     * @param request 配置用户角色请求参数
     */
    @PutMapping("/assign-roles")
    public void assignRoles(@Valid @RequestBody AssignRolesRequest request) {
        AssignRolesDTO assignRolesDTO = userConverter.toAssignRolesDTO(request);
        sysUserService.assignRoles(assignRolesDTO);
    }

}
