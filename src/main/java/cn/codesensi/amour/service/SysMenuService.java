package cn.codesensi.amour.service;

import cn.codesensi.amour.model.entity.SysMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 路由菜单表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 返回一个账号所拥有的权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> listPermCodeByUserId(Long userId);

    /**
     * 查询用户路由菜单列表
     *
     * @param userId 用户id
     * @return 路由菜单列表
     */
    List<SysMenu> listMenuByUserId(Long userId);

    /**
     * 获取菜单的所有祖先ID（包含自身）
     *
     * @param menuId 菜单ID
     * @return 菜单的所有祖先ID（包含自身）
     */
    Set<Long> listAncestorIdsById(Long menuId);

    /**
     * 批量获取多个菜单的所有祖先ID（并集，去重）
     *
     * @param menuIds 菜单ID列表
     * @return 菜单的所有祖先ID（包含自身）
     */
    Set<Long> listAncestorIdsByIds(List<Long> menuIds);

    /**
     * 失效指定用户的权限编码缓存（perm 缓存）
     *
     * @param userIds 用户ID列表
     */
    void evictPermCache(List<Long> userIds);

    /**
     * 失效指定用户的路由菜单缓存（menu 缓存）
     *
     * @param userIds 用户ID列表
     */
    void evictMenuCache(List<Long> userIds);

}
