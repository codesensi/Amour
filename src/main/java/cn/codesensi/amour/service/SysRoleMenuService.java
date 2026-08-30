package cn.codesensi.amour.service;

import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysRoleMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 角色菜单关联表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 根据角色编码列表获取权限菜单列表
     *
     * @param roleCodeList 角色编码列表
     * @return 权限菜单列表
     */
    List<String> listPermCodeByRoleCodeList(List<String> roleCodeList);

    /**
     * 根据角色编码列表获取菜单列表
     *
     * @param roleCodeList 角色编码列表
     * @return 菜单列表
     */
    List<SysMenu> listMenuByRoleCodeList(List<String> roleCodeList);

}
