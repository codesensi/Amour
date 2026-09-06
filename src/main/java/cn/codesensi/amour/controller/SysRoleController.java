package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.RoleConverter;
import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleInsertDTO;
import cn.codesensi.amour.model.dto.RolePageDTO;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.request.AssignMenusRequest;
import cn.codesensi.amour.model.request.RoleInsertRequest;
import cn.codesensi.amour.model.request.RolePageRequest;
import cn.codesensi.amour.model.response.RolePageResponse;
import cn.codesensi.amour.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
     * 分页查询角色信息表。
     * <p>
     * 角色名称、角色编码为模糊匹配,状态为精确匹配,条件缺省时自动忽略。
     *
     * @param rolePageRequest 分页查询参数
     * @return 分页对象
     */
    @GetMapping("/page")
    public Page<RolePageResponse> page(@Valid RolePageRequest rolePageRequest) {
        RolePageDTO rolePageDTO = roleConverter.toPageDTO(rolePageRequest);
        Page<SysRole> sysRolePage = sysRoleService.page(rolePageDTO);
        return roleConverter.toPageResponse(sysRolePage);
    }

    /**
     * 新增角色信息
     *
     * @param request 新增角色请求参数
     */
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody RoleInsertRequest request) {
        RoleInsertDTO roleInsertDTO = roleConverter.toInsertDTO(request);
        sysRoleService.insert(roleInsertDTO);
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

    /**
     * 查询角色已分配的菜单ID列表（授权弹窗预勾选用）。
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/menu-ids/{id}")
    public List<String> menuIds(@PathVariable Long id) {
        return sysRoleService.listMenuIdsByRoleId(id)
                .stream().map(String::valueOf).toList();
    }

}
