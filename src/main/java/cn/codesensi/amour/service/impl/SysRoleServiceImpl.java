package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.model.converter.SysRoleConverter;
import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleSaveDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysRoleMenu;
import cn.codesensi.amour.service.*;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;


/**
 * 角色信息表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleConverter sysRoleConverter;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;

    /**
     * 保存角色信息
     *
     * @param roleSaveDTO 角色信息
     */
    @Override
    public void saveRole(RoleSaveDTO roleSaveDTO) {
        String code = roleSaveDTO.getCode();
        // 校验角色编码是否存在
        long count = QueryChain.of(sysRoleMapper)
                .where(SYS_ROLE.CODE.eq(code))
                .count();
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole sysRole = sysRoleConverter.toEntity(roleSaveDTO);
        sysRoleMapper.insert(sysRole, true);
    }

    /**
     * 分配角色菜单权限。
     * <p>
     * 删除旧关联与写入新关联处于同一事务，原子提交，避免中途失败留下"旧关联已删、新关联未插"的半状态；
     * 缓存失效注册在事务提交后执行，避免提交前其他请求回源查库把中间状态重新写入缓存。
     *
     * @param assignMenusDTO 角色菜单权限信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignMenus(AssignMenusDTO assignMenusDTO) {
        Long roleId = assignMenusDTO.getRoleId();
        // 1. 校验角色是否存在
        SysRole sysRole = QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.BUILTIN)
                .where(SYS_ROLE.ID.eq(roleId))
                .one();
        if (ObjUtil.isNull(sysRole)) {
            throw new BusinessException("角色不存在");
        }

        // 系统内置角色不允许修改权限
        if (BuiltinEnum.YES.getCode().equals(sysRole.getBuiltin())) {
            throw new BusinessException("系统内置角色不允许修改权限");
        }

        List<Long> menuIds = assignMenusDTO.getMenuIds();
        // 2. 校验待分配的菜单是否存在（避免产生悬空关联）
        if (CollUtil.isNotEmpty(menuIds)) {
            // 菜单ID去重
            menuIds = menuIds.stream().distinct().toList();
            checkMenusExist(menuIds);
        }

        // 3. 删除旧关联
        sysRoleMenuService.remove(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        // 查询角色下属所有用户ID，用于在关联变更后失效其权限缓存
        List<Long> userIds = sysUserRoleService.queryChain()
                .select(SYS_USER_ROLE.USER_ID)
                .where(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                .listAs(Long.class);

        // 4. 补全所有父菜单
        Set<Long> allMenuIds = new HashSet<>();
        if (CollUtil.isNotEmpty(menuIds)) {
            // 获取所有菜单的祖先ID（包含自身）
            Set<Long> ancestors = sysMenuService.listAncestorIdsByIds(menuIds);
            allMenuIds.addAll(ancestors);
        }
        if (CollUtil.isNotEmpty(allMenuIds)) {
            // 5. 插入新关联（如果菜单列表为空，则仅删除）
            List<SysRoleMenu> entities = allMenuIds.stream()
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(roleId);
                        sysRoleMenu.setMenuId(menuId);
                        return sysRoleMenu;
                    }).toList();
            // 批量插入
            sysRoleMenuService.saveBatch(entities);
        }

        // 6. 失效角色下属所有用户的权限/路由菜单/用户信息缓存（菜单关联变更影响权限码与可访问菜单；角色码不变，role 缓存无需清理）；
        //    注册到事务提交后执行，避免提交前其他请求回源查库把中间状态重新写入缓存
        CacheUtil.evictAfterCommit(() -> {
            sysMenuService.evictPermCache(userIds);
            sysMenuService.evictMenuCache(userIds);
            userIds.forEach(sysUserService::evictUserCache);
        });
    }

    /**
     * 校验待分配的菜单是否都存在（逻辑删除的菜单视为不存在），避免产生悬空关联。
     *
     * @param menuIds 去重后的菜单ID列表
     */
    private void checkMenusExist(List<Long> menuIds) {
        Set<Long> existingIds = sysMenuService.listByIds(menuIds).stream()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());
        List<Long> missingIds = menuIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();
        if (CollUtil.isNotEmpty(missingIds)) {
            throw new BusinessException("菜单不存在：" + missingIds);
        }
    }

}
