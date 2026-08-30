package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.model.converter.SysRoleConverter;
import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleSaveDTO;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysRoleMenu;
import cn.codesensi.amour.service.SysMenuService;
import cn.codesensi.amour.service.SysRoleMenuService;
import cn.codesensi.amour.service.SysRoleService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * 分配角色菜单权限
     *
     * @param assignMenusDTO 角色菜单权限信息
     */
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

        // 2. 删除旧关联
        sysRoleMenuService.remove(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        // 同步清除角色下属所有用户的相关缓存（权限、菜单、用户信息）
        List<Long> userIds = sysUserRoleService.queryChain()
                .select(SYS_USER_ROLE.USER_ID)
                .where(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                .listAs(Long.class);
        // TODO 按照用户id清理缓存

        List<Long> menuIds = assignMenusDTO.getMenuIds();
        // 3. 补全所有父菜单
        Set<Long> allMenuIds = new HashSet<>();
        // 如果菜单列表为空，则仅删除旧关联
        if (CollUtil.isNotEmpty(menuIds)) {
            // 菜单ID去重
            menuIds = menuIds.stream().distinct().toList();
            // 获取所有菜单的祖先ID（包含自身）
            Set<Long> ancestors = sysMenuService.listAncestorIdsByIds(menuIds);
            allMenuIds.addAll(ancestors);
        }
        if (CollUtil.isNotEmpty(allMenuIds)) {
            // 4. 插入新关联（如果菜单列表为空，则仅删除）
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
    }

}
