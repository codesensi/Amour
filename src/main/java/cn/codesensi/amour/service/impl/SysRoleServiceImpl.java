package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.model.converter.RoleConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysRoleMenu;
import cn.codesensi.amour.service.*;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final RoleConverter roleConverter;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysMenuService sysMenuService;
    private final CacheEvictService cacheEvictService;

    /**
     * 分页查询角色信息。
     * <p>
     * 角色名称、角色编码为模糊匹配，状态为精确匹配，条件缺省时自动忽略；
     * 页码与每页条数的缺省值由 {@link BasePage} 提供(1 与 20)，与前端默认值保持一致。
     *
     * @param rolePageDTO 分页查询参数
     * @return 角色信息分页结果
     */
    @Override
    public Page<SysRole> page(RolePageDTO rolePageDTO) {
        return QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ALL_COLUMNS)
                .where(SYS_ROLE.NAME.like(rolePageDTO.getName(), StrUtil::isNotBlank))
                .and(SYS_ROLE.CODE.like(rolePageDTO.getCode(), StrUtil::isNotBlank))
                .and(SYS_ROLE.STATUS.eq(rolePageDTO.getStatus(), ObjUtil::isNotNull))
                .page(Page.of(rolePageDTO.getPageNumber(), rolePageDTO.getPageSize()));
    }

    /**
     * 查询全部角色列表。
     * <p>
     * 按 sort 升序、id 升序排序，保证选项顺序稳定。
     *
     * @return 全量角色列表
     */
    @Override
    public List<SysRole> list() {
        return QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ALL_COLUMNS)
                .orderBy(SYS_ROLE.SORT, true)
                .orderBy(SYS_ROLE.ID, true)
                .list();
    }

    /**
     * 新增角色信息
     *
     * @param roleInsertDTO 角色信息
     */
    @Override
    public void insert(RoleInsertDTO roleInsertDTO) {
        String code = roleInsertDTO.getCode();
        // 校验角色编码未被占用（含已删除记录，全生命周期唯一）
        long count = LogicDeleteManager.execWithoutLogicDelete(() ->
                QueryChain.of(sysRoleMapper)
                        .where(SYS_ROLE.CODE.eq(code))
                        .count());
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole sysRole = roleConverter.toEntity(roleInsertDTO);
        sysRoleMapper.insert(sysRole, true);
    }

    /**
     * 修改角色信息。
     * <p>
     * 角色编码创建后不可修改，仅更新名称/排序/备注等资料字段。
     *
     * @param roleUpdateDTO 角色信息
     */
    @Override
    public void update(RoleUpdateDTO roleUpdateDTO) {
        SysRole sysRole = getById(roleUpdateDTO.getId());
        if (ObjUtil.isNull(sysRole)) {
            throw new BusinessException("角色不存在");
        }

        SysRole entity = roleConverter.toEntity(roleUpdateDTO);
        updateById(entity);
    }

    /**
     * 修改角色状态。
     * <p>
     * 系统内置角色不允许禁用(启用请求不受限)；同状态幂等返回；
     * 状态变化影响该角色下用户的权限，失效其权限/路由菜单/用户信息缓存。
     *
     * @param roleChangeStatusDTO 角色状态信息
     */
    @Override
    public void changeStatus(RoleChangeStatusDTO roleChangeStatusDTO) {
        SysRole sysRole = getById(roleChangeStatusDTO.getId());
        if (ObjUtil.isNull(sysRole)) {
            throw new BusinessException("角色不存在");
        }

        // 系统内置角色不允许禁用(启用请求不受限)
        if (BuiltinEnum.YES.getCode().equals(sysRole.getBuiltin())
                && EnableEnum.DISABLE.getCode().equals(roleChangeStatusDTO.getStatus())) {
            throw new BusinessException("系统内置角色不允许禁用");
        }

        // 状态一致时幂等返回
        if (roleChangeStatusDTO.getStatus().equals(sysRole.getStatus())) {
            return;
        }

        // 先查询受影响用户再写库，确保写库成功后缓存失效动作必然可执行
        List<Long> userIds = listUserIdsByRoleIds(List.of(roleChangeStatusDTO.getId()));

        SysRole entity = new SysRole();
        entity.setId(roleChangeStatusDTO.getId());
        entity.setStatus(roleChangeStatusDTO.getStatus());
        updateById(entity);
        CacheUtil.evictAfterCommit(() -> {
            log.debug("角色状态变更完成：roleId={}，status={}，失效缓存，受影响用户数={}",
                    roleChangeStatusDTO.getId(), roleChangeStatusDTO.getStatus(), userIds.size());
            cacheEvictService.evictPermCache(userIds);
            cacheEvictService.evictMenuCache(userIds);
            cacheEvictService.evictUserCache(userIds);
        });
    }

    /**
     * 批量删除角色信息。
     * <p>
     * 删除角色同时清理角色-菜单、用户-角色关联；缓存失效与踢会话注册到事务提交后执行，
     * 避免提交前其他请求回源查库把中间状态重新写入缓存。
     *
     * @param ids 角色ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> ids) {
        // 1. 校验角色是否存在
        List<SysRole> sysRoles = listByIds(ids);
        List<Long> existingIds = sysRoles.stream().map(SysRole::getId).toList();
        List<Long> missingIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();
        if (CollUtil.isNotEmpty(missingIds)) {
            throw new BusinessException("角色不存在：" + missingIds);
        }

        // 2. 系统内置角色不允许删除(整批失败)
        boolean containsBuiltin = sysRoles.stream()
                .anyMatch(sysRole -> BuiltinEnum.YES.getCode().equals(sysRole.getBuiltin()));
        if (containsBuiltin) {
            throw new BusinessException("系统内置角色不允许删除");
        }

        // 3. 查询这些角色下关联的用户ID,用于删除后失效其缓存
        List<Long> userIds = listUserIdsByRoleIds(ids);

        // 4. 删除角色与角色-菜单、用户-角色关联
        removeByIds(ids);
        sysRoleMenuService.remove(SYS_ROLE_MENU.ROLE_ID.in(ids));
        sysUserRoleService.remove(SYS_USER_ROLE.ROLE_ID.in(ids));

        // 5. 失效受影响用户的权限/路由菜单/用户信息缓存,并踢出其会话(注册到事务提交后执行)
        CacheUtil.evictAfterCommit(() -> {
            log.debug("角色删除完成：roleIds={}，失效缓存，受影响用户数={}", ids, userIds.size());
            cacheEvictService.evictPermCache(userIds);
            cacheEvictService.evictMenuCache(userIds);
            cacheEvictService.evictUserCache(userIds);
            // 角色被删除后其关联用户的权限已变化,踢出使其重新登录
            userIds.forEach(StpUtil::logout);
        });
    }

    /**
     * 查询角色列表下关联的所有用户ID。
     *
     * @param roleIds 角色ID列表
     * @return 用户ID列表
     */
    private List<Long> listUserIdsByRoleIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }
        return sysUserRoleService.queryChain()
                .select(SYS_USER_ROLE.USER_ID)
                .where(SYS_USER_ROLE.ROLE_ID.in(roleIds))
                .listAs(Long.class);
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
            log.debug("角色菜单关联变更完成：roleId={}，补全祖先菜单数={}，失效缓存，受影响用户数={}",
                    roleId, allMenuIds.size(), userIds.size());
            cacheEvictService.evictPermCache(userIds);
            cacheEvictService.evictMenuCache(userIds);
            cacheEvictService.evictUserCache(userIds);
        });
    }

    /**
     * 查询角色已分配的菜单ID列表。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return sysRoleMenuService.listMenuIdsByRoleId(roleId);
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