package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.RoleConverter;
import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleSaveDTO;
import cn.codesensi.amour.model.request.AssignMenusRequest;
import cn.codesensi.amour.model.request.RoleSaveRequest;
import cn.codesensi.amour.service.SysRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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
     * 保存角色信息
     *
     * @param request 角色信息
     */
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody RoleSaveRequest request) {
        RoleSaveDTO roleSaveDTO = roleConverter.toSaveDTO(request);
        sysRoleService.insert(roleSaveDTO);
    }

    /**
     * 分配角色菜单权限
     *
     * @param request 角色菜单权限信息
     */
    @PutMapping("/assign-menus")
    public void assignMenus(@Valid @RequestBody AssignMenusRequest request) {
        AssignMenusDTO assignMenusDTO = roleConverter.toAssignMenusDTO(request);
        sysRoleService.assignMenus(assignMenusDTO);
    }

}
