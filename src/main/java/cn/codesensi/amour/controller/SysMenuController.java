package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.MenuConverter;
import cn.codesensi.amour.model.dto.MenuChangeStatusDTO;
import cn.codesensi.amour.model.dto.MenuInsertDTO;
import cn.codesensi.amour.model.dto.MenuUpdateDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.request.MenuChangeStatusRequest;
import cn.codesensi.amour.model.request.MenuInsertRequest;
import cn.codesensi.amour.model.request.MenuUpdateRequest;
import cn.codesensi.amour.model.response.MenuResponse;
import cn.codesensi.amour.service.SysMenuService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路由菜单表 控制层。
 *
 * @author codesensi
 * @since 1.0
 */
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;
    private final MenuConverter menuConverter;

    /**
     * 查询全部菜单列表。
     * <p>
     * 返回全量菜单的一维扁平数组（id + pid），由前端自行组树；
     * 树形列表数据量小，不做分页。
     *
     * @return 菜单列表
     */
    @SaCheckPermission("system:menu:page")
    @GetMapping("/list")
    public List<MenuResponse> list() {
        List<SysMenu> sysMenus = sysMenuService.list();
        return menuConverter.toListResponse(sysMenus);
    }

    /**
     * 新增菜单
     *
     * @param request 新增菜单请求参数
     */
    @SaCheckPermission("system:menu:insert")
    @Log(module = "菜单管理", operation = "新增菜单", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@Valid @RequestBody MenuInsertRequest request) {
        MenuInsertDTO menuInsertDTO = menuConverter.toInsertDTO(request);
        sysMenuService.insert(menuInsertDTO);
    }

    /**
     * 修改菜单
     *
     * @param request 修改菜单请求参数
     */
    @SaCheckPermission("system:menu:update")
    @Log(module = "菜单管理", operation = "修改菜单", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@Valid @RequestBody MenuUpdateRequest request) {
        MenuUpdateDTO menuUpdateDTO = menuConverter.toUpdateDTO(request);
        sysMenuService.update(menuUpdateDTO);
    }

    /**
     * 修改菜单状态
     *
     * @param request 修改菜单状态请求参数
     */
    @SaCheckPermission("system:menu:update")
    @Log(module = "菜单管理", operation = "修改菜单状态", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-status")
    public void changeStatus(@Valid @RequestBody MenuChangeStatusRequest request) {
        MenuChangeStatusDTO menuChangeStatusDTO = menuConverter.toChangeStatusDTO(request);
        sysMenuService.changeStatus(menuChangeStatusDTO);
    }

    /**
     * 删除菜单(级联删除其全部下级菜单)
     *
     * @param id 菜单ID
     */
    @SaCheckPermission("system:menu:delete")
    @Log(module = "菜单管理", operation = "删除菜单", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        sysMenuService.delete(id);
    }
}