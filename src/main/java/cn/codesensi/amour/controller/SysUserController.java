package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.UserConverter;
import cn.codesensi.amour.model.dto.AssignRolesDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserSaveDTO;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.request.AssignRolesRequest;
import cn.codesensi.amour.model.request.UserSaveRequest;
import cn.codesensi.amour.model.response.UserInfoResponse;
import cn.codesensi.amour.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;


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
     * 根据主键删除用户信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return sysUserService.removeById(id);
    }

    /**
     * 根据主键更新用户信息表。
     *
     * @param sysUser 用户信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/update")
    public boolean update(@Valid @RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser);
    }

    /**
     * 根据主键获取用户信息表。
     *
     * @param id 用户信息表主键
     * @return 用户信息表详情
     */
    @GetMapping("/detail/{id}")
    public SysUser detail(@PathVariable Long id) {
        return sysUserService.getById(id);
    }

    /**
     * 分页查询用户信息表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<SysUser> page(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              SysUser sysUser) {
        Page<SysUser> page = new Page<>(pageNumber, pageSize);
        return sysUserService.queryChain()
                .select(SYS_USER.ALL_COLUMNS)
                .from(SYS_USER)
                // TODO 查询条件
                // .where(SYS_USER.ID.eq(sysUser.getId()))
                .page(page);
    }

    /**
     * 获取当前用户信息
     *
     * @return SysUser 用户信息
     */
    @GetMapping("/getCurrentUser")
    public UserInfoResponse getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        UserInfoDTO userInfoDTO = sysUserService.getCurrentUser(userId);
        return userConverter.toInfoResponse(userInfoDTO);
    }

    /**
     * 保存用户信息
     *
     * @param request 保存用户请求参数
     */
    @PostMapping("/saveUser")
    public void saveUser(@Valid @RequestBody UserSaveRequest request) {
        UserSaveDTO userSaveDTO = userConverter.toSaveDTO(request);
        sysUserService.saveUser(userSaveDTO);
    }

    /**
     * 配置用户角色
     *
     * @param request 配置用户角色请求参数
     */
    @PutMapping("/assignRoles")
    public void assignRoles(@RequestBody @Valid AssignRolesRequest request) {
        AssignRolesDTO assignRolesDTO = userConverter.toAssignRolesDTO(request);
        sysUserService.assignRoles(assignRolesDTO);
    }

}
