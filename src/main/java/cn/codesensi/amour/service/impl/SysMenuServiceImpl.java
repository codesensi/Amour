package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.enums.MenuType;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysMenuMapper;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.service.SysMenuService;
import cn.codesensi.amour.service.SysRoleMenuService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysMenuTableDef.SYS_MENU;

/**
 * 路由菜单表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysUserRoleService sysUserRoleService;
    private final CacheManager cacheManager;

    /**
     * 返回一个账号所拥有的权限编码列表。
     * <p>
     * 结果经 perm 缓存加速（Key 为用户ID），写后 30 天兜底过期，写侧显式失效；
     * 缓存未注册/未就绪时降级为直接查库。
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Override
    public List<String> listPermCodeByUserId(Long userId) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.PERM));
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            return loadPermCodes(userId);
        }
        // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿
        return cache.get(userId, () -> loadPermCodes(userId));
    }

    /**
     * 从库中加载权限编码列表（超级管理员短路）。
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    private List<String> loadPermCodes(Long userId) {
        // 获取去重后的角色列表
        List<SysRole> sysRoles = sysUserRoleService.listRoleByUserId(userId);
        // 获取角色编码列表
        List<String> roleCodeList = sysRoles.stream()
                .map(SysRole::getCode)
                .filter(StrUtil::isNotBlank)
                .toList();
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }

        // 超级管理员角色的权限码
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return List.of(RbacConst.PERM_ADMIN_CODE);
        }

        // 获取角色拥有的权限码列表
        return sysRoleMenuService.listPermCodeByRoleCodeList(roleCodeList);
    }

    /**
     * 失效指定用户的权限编码缓存。
     *
     * @param userIds 用户ID列表
     */
    @Override
    public void evictPermCache(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.PERM));
        if (cache == null) {
            return;
        }
        for (Long userId : userIds) {
            cache.evict(userId);
        }
    }

    /**
     * 查询用户路由菜单列表。
     * <p>
     * 结果经 menu 缓存加速（Key 为用户ID），写后 30 天兜底过期，写侧显式失效；
     * 缓存未注册/未就绪时降级为直接查库。
     *
     * @param userId 用户id
     * @return 路由菜单列表
     */
    @Override
    public List<SysMenu> listMenuByUserId(Long userId) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.MENU));
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            return loadMenus(userId);
        }
        // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿
        return cache.get(userId, () -> loadMenus(userId));
    }

    /**
     * 从库中加载用户路由菜单列表（超级管理员查看全部非按钮菜单）。
     *
     * @param userId 用户id
     * @return 路由菜单列表
     */
    private List<SysMenu> loadMenus(Long userId) {
        // 获取用户的角色编码列表
        List<String> roleCodeList = sysUserRoleService.listRoleCodeByUserId(userId);
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }

        // 超级管理员角色可查看所有菜单（包含已禁用的目录和菜单、不包含按钮级别）
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return QueryChain.of(sysMenuMapper)
                    .select(SYS_MENU.ALL_COLUMNS)
                    // 排除按钮类型
                    .where(SYS_MENU.TYPE.ne(MenuType.B.getCode()))
                    .orderBy(SYS_MENU.SORT, true)
                    .list();
        }

        // 获取角色拥有的路由菜单列表（不包含按钮级别）
        return sysRoleMenuService.listMenuByRoleCodeList(roleCodeList);
    }

    /**
     * 失效指定用户的路由菜单缓存。
     *
     * @param userIds 用户ID列表
     */
    @Override
    public void evictMenuCache(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.MENU));
        if (cache == null) {
            return;
        }
        for (Long userId : userIds) {
            cache.evict(userId);
        }
    }

    /**
     * 获取菜单的所有祖先ID（包含自身）
     */
    @Override
    public Set<Long> listAncestorIdsById(Long menuId) {
        Set<Long> ancestorIds = new HashSet<>();
        SysMenu sysMenu = QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .where(SYS_MENU.ID.eq(menuId))
                .one();
        while (sysMenu != null && !AppConst.ZERO_LONG.equals(sysMenu.getId())) {
            ancestorIds.add(sysMenu.getId());
            if (AppConst.ZERO_LONG.equals(sysMenu.getPid())) {
                break;
            }
            sysMenu = QueryChain.of(sysMenuMapper)
                    .select(SYS_MENU.ALL_COLUMNS)
                    .where(SYS_MENU.ID.eq(sysMenu.getPid()))
                    .one();
        }
        return ancestorIds;
    }

    /**
     * 批量获取多个菜单的所有祖先ID（并集，去重）
     */
    @Override
    public Set<Long> listAncestorIdsByIds(List<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return Collections.emptySet();
        }
        return menuIds.stream()
                .map(this::listAncestorIdsById)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
