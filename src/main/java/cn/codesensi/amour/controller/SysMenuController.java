package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.MenuConverter;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.response.MenuResponse;
import cn.codesensi.amour.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 路由菜单表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
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
     * 树形列表数据量小,不做分页。
     *
     * @return 菜单列表
     */
    @GetMapping("/list")
    public List<MenuResponse> list() {
        List<SysMenu> sysMenus = sysMenuService.list();
        return menuConverter.toListResponse(sysMenus);
    }
}
