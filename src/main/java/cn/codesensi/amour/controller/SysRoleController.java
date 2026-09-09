package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.RoleConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.RolePageResponse;
import cn.codesensi.amour.model.response.RoleResponse;
import cn.codesensi.amour.service.SysRoleService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 角色信息表 控制层。
 *
 * @author codesensi
 * @since 1.0
 */
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final RoleConverter roleConverter;

    /**
     * 分页查询角色信息表。
     * <p>
     * 角色名称、角色编码为模糊匹配，状态为精确匹配，条件缺省时自动忽略。
     *
     * @param rolePageRequest 分页查询参数
     * @return 分页对象
     */
    @SaCheckPermission("system:role:page")
    @GetMapping("/page")
    public Page<RolePageResponse> page(@Valid RolePageRequest rolePageRequest) {
        RolePageDTO rolePageDTO = roleConverter.toPageDTO(rolePageRequest);
        Page<SysRole> sysRolePage = sysRoleService.page(rolePageDTO);
        return roleConverter.toPageResponse(sysRolePage);
    }

    /**
     * 查询全部角色列表。
     * <p>
     * 返回全量角色的一维数组，用作分配角色等场景的选项数据源；
     * 数据量小，不做分页。
     *
     * @return 角色列表
     */
    @SaCheckPermission("system:role:page")
    @GetMapping("/list")
    public List<RoleResponse> list() {
        List<SysRole> sysRoles = sysRoleService.list();
        return roleConverter.toListResponse(sysRoles);
    }

    /**
     * 新增角色信息
     *
     * @param request 新增角色请求参数
     */
    @SaCheckPermission("system:role:insert")
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody RoleInsertRequest request) {
        RoleInsertDTO roleInsertDTO = roleConverter.toInsertDTO(request);
        sysRoleService.insert(roleInsertDTO);
    }

    /**
     * 修改角色信息
     *
     * @param request 修改角色请求参数
     */
    @SaCheckPermission("system:role:update")
    @PutMapping("/update")
    public void update(@Valid @RequestBody RoleUpdateRequest request) {
        RoleUpdateDTO roleUpdateDTO = roleConverter.toUpdateDTO(request);
        sysRoleService.update(roleUpdateDTO);
    }

    /**
     * 修改角色状态
     *
     * @param request 角色状态请求参数
     */
    @SaCheckPermission("system:role:update")
    @PutMapping("/change-status")
    public void changeStatus(@Valid @RequestBody RoleChangeStatusRequest request) {
        RoleChangeStatusDTO roleChangeStatusDTO = roleConverter.toChangeStatusDTO(request);
        sysRoleService.changeStatus(roleChangeStatusDTO);
    }

    /**
     * 批量删除角色信息
     * <p>
     * 路径参数支持英文逗号分隔的多个ID，如 /sys/role/delete/1,2,3。
     *
     * @param ids 角色ID列表
     */
    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        sysRoleService.delete(List.of(ids));
    }

    /**
     * 分配角色菜单权限
     *
     * @param request 角色菜单权限信息
     */
    @SaCheckPermission("system:role:update")
    @PutMapping("/assign-menus")
    public void assignMenus(@Valid @RequestBody AssignMenusRequest request) {
        AssignMenusDTO assignMenusDTO = roleConverter.toAssignMenusDTO(request);
        sysRoleService.assignMenus(assignMenusDTO);
    }

    /**
     * 查询角色已分配的菜单ID列表（授权弹窗预勾选用）。
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @SaCheckPermission("system:role:update")
    @GetMapping("/menu-ids/{id}")
    public List<String> menuIds(@PathVariable Long id) {
        return sysRoleService.listMenuIdsByRoleId(id)
                .stream().map(String::valueOf).toList();
    }

}