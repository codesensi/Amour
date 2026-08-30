package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.service.SysMenuService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.codesensi.amour.model.entity.table.SysMenuTableDef.SYS_MENU;

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

    /**
     * 分页查询路由菜单表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @param sysMenu    路由菜单表
     * @return 分页对象
     */
    @GetMapping("/page")
    public Page<SysMenu> page(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              SysMenu sysMenu) {
        Page<SysMenu> page = new Page<>(pageNumber, pageSize);
        return sysMenuService.queryChain()
                .select(SYS_MENU.ALL_COLUMNS)
                .from(SYS_MENU)
                // TODO 查询条件
                // .where(SYS_MENU.ID.eq(sysMenu.getId()))
                .page(page);
    }

}
