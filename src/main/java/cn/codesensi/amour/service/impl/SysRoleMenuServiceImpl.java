package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.enums.MenuType;
import cn.codesensi.amour.mapper.SysMenuMapper;
import cn.codesensi.amour.mapper.SysRoleMenuMapper;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysRoleMenu;
import cn.codesensi.amour.service.SysRoleMenuService;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysMenuTableDef.SYS_MENU;
import static cn.codesensi.amour.model.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;


/**
 * 角色菜单关联表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final SysMenuMapper sysMenuMapper;

    /**
     * 根据角色编码列表获取权限菜单列表
     *
     * @param roleCodeList 角色编码列表
     * @return 权限菜单列表
     */
    @Override
    public List<String> listPermCodeByRoleCodeList(List<String> roleCodeList) {
        return QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .leftJoin(SYS_ROLE_MENU).on(SYS_ROLE_MENU.MENU_ID.eq(SYS_MENU.ID))
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ID.eq(SYS_ROLE_MENU.ROLE_ID))
                .where(SYS_ROLE.CODE.in(roleCodeList))
                .and(SYS_ROLE.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .and(SYS_MENU.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .orderBy(SYS_MENU.SORT, true)
                .list()
                .stream()
                .map(SysMenu::getPerms)
                .filter(StrUtil::isNotBlank)
                .toList();
    }

    /**
     * 根据角色编码列表获取菜单列表
     *
     * @param roleCodeList 角色编码列表
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> listMenuByRoleCodeList(List<String> roleCodeList) {
        return QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .leftJoin(SYS_ROLE_MENU).on(SYS_ROLE_MENU.MENU_ID.eq(SYS_MENU.ID))
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ID.eq(SYS_ROLE_MENU.ROLE_ID))
                .where(SYS_ROLE.CODE.in(roleCodeList))
                .and(SYS_ROLE.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .and(SYS_MENU.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .and(SYS_MENU.TYPE.ne(MenuType.B.getCode()))
                .orderBy(SYS_MENU.SORT, true)
                .list();
    }
}
