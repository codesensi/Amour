package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.RoleConverter;
import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleSaveDTO;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.request.AssignMenusRequest;
import cn.codesensi.amour.model.request.RoleSaveRequest;
import cn.codesensi.amour.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;


/**
 * 角色信息表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final RoleConverter roleConverter;

    /**
     * 根据主键删除角色信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return sysRoleService.removeById(id);
    }

    /**
     * 根据主键更新角色信息表。
     *
     * @param sysRole 角色信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/update")
    public boolean update(@Valid @RequestBody SysRole sysRole) {
        return sysRoleService.updateById(sysRole);
    }

    /**
     * 根据主键获取角色信息表。
     *
     * @param id 角色信息表主键
     * @return 角色信息表详情
     */
    @GetMapping("/detail/{id}")
    public SysRole detail(@PathVariable Long id) {
        return sysRoleService.getById(id);
    }

    /**
     * 分页查询角色信息表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @param sysRole    角色信息表
     * @return 分页对象
     */
    @GetMapping("/page")
    public Page<SysRole> page(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              SysRole sysRole) {
        Page<SysRole> page = new Page<>(pageNumber, pageSize);
        return sysRoleService.queryChain()
                .select(SYS_ROLE.ALL_COLUMNS)
                .from(SYS_ROLE)
                // TODO 查询条件
                // .where(SYS_ROLE.ID.eq(sysRole.getId()))
                .page(page);
    }

    /**
     * 保存角色信息
     *
     * @param request 角色信息
     */
    @PostMapping("/saveRole")
    public void saveRole(@Valid @RequestBody RoleSaveRequest request) {
        RoleSaveDTO roleSaveDTO = roleConverter.toSaveDTO(request);
        sysRoleService.saveRole(roleSaveDTO);
    }

    /**
     * 分配角色菜单权限
     *
     * @param request 角色菜单权限信息
     */
    @PutMapping("/assignMenus")
    public void assignMenus(@Valid @RequestBody AssignMenusRequest request) {
        AssignMenusDTO assignMenusDTO = roleConverter.toAssignMenusDTO(request);
        sysRoleService.assignMenus(assignMenusDTO);
    }

}
