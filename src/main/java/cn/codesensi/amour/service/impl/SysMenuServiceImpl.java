package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.enums.MenuType;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysMenuMapper;
import cn.codesensi.amour.model.converter.MenuConverter;
import cn.codesensi.amour.model.dto.MenuChangeStatusDTO;
import cn.codesensi.amour.model.dto.MenuInsertDTO;
import cn.codesensi.amour.model.dto.MenuUpdateDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.service.SysMenuService;
import cn.codesensi.amour.service.SysRoleMenuService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysMenuTableDef.SYS_MENU;
import static cn.codesensi.amour.model.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;

/**
 * 路由菜单表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysUserRoleService sysUserRoleService;
    private final CacheManager cacheManager;
    private final MenuConverter menuConverter;

    /**
     * 查询全部菜单列表。
     * <p>
     * 覆写 {@link IService#list()}：按 sort 升序、id 升序排序,保证菜单树展示顺序稳定。
     *
     * @return 全量菜单列表
     */
    @Override
    public List<SysMenu> list() {
        return QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .orderBy(SYS_MENU.SORT, true)
                .orderBy(SYS_MENU.ID, true)
                .list();
    }

    /**
     * 新增菜单。
     * <p>
     * 校验上级菜单合法性与类型必填项;新增菜单尚未关联角色,无需失效用户缓存。
     *
     * @param menuInsertDTO 菜单信息
     */
    @Override
    public void insert(MenuInsertDTO menuInsertDTO) {
        validatePid(menuInsertDTO.getPid());
        validateTypeRequired(menuInsertDTO.getType(), menuInsertDTO.getPath(), menuInsertDTO.getPerms());
        // 同级下菜单名称唯一,避免树展示与定位歧义;权限标识全局唯一,避免前端 hasPerms 按钮门控错乱
        validateSiblingTitleUnique(menuInsertDTO.getPid(), menuInsertDTO.getTitle(), null);
        validatePermsUnique(menuInsertDTO.getPerms(), null);

        SysMenu sysMenu = menuConverter.toEntity(menuInsertDTO);
        // 内置标识仅随种子数据下发,新增数据一律为非内置
        sysMenu.setBuiltin(BuiltinEnum.NO.getCode());
        if (ObjUtil.isNull(sysMenu.getSort())) {
            sysMenu.setSort(AppConst.ZERO_INT);
        }
        if (ObjUtil.isNull(sysMenu.getStatus())) {
            sysMenu.setStatus(EnableEnum.ENABLE.getCode());
        }
        if (ObjUtil.isNull(sysMenu.getHidden())) {
            sysMenu.setHidden(AppConst.ZERO_INT);
        }
        sysMenuMapper.insert(sysMenu, true);
    }

    /**
     * 修改菜单。
     * <p>
     * 菜单类型不在修改入参中(结构性标识,创建后不可变);系统内置菜单的结构字段(上级/路由路径/组件路径/权限编码)
     * 不允许修改;上级菜单不允许选择自身或其下级(否则树成环);
     * 变更影响权限码与路由装配,失效全部用户的 perm/menu 缓存。
     *
     * @param menuUpdateDTO 菜单信息
     */
    @Override
    public void update(MenuUpdateDTO menuUpdateDTO) {
        SysMenu sysMenu = getById(menuUpdateDTO.getId());
        if (ObjUtil.isNull(sysMenu)) {
            throw new BusinessException("菜单不存在");
        }

        // 系统内置菜单的结构字段与源码路由/前后端权限契约绑定,不允许修改
        if (BuiltinEnum.YES.getCode().equals(sysMenu.getBuiltin())) {
            validateBuiltinUnchanged(sysMenu, menuUpdateDTO);
        }

        // 类型取库中现值(创建后不可变),此处仅校验按类型的必填项
        validateTypeRequired(sysMenu.getType(), menuUpdateDTO.getPath(), menuUpdateDTO.getPerms());
        validatePid(menuUpdateDTO.getPid());
        // 同级下菜单名称唯一,避免树展示与定位歧义;权限标识全局唯一,避免前端 hasPerms 按钮门控错乱
        validateSiblingTitleUnique(menuUpdateDTO.getPid(), menuUpdateDTO.getTitle(), menuUpdateDTO.getId());
        validatePermsUnique(menuUpdateDTO.getPerms(), menuUpdateDTO.getId());

        // 上级菜单不能选择自身或其下级菜单,否则树成环
        if (menuUpdateDTO.getId().equals(menuUpdateDTO.getPid())) {
            throw new BusinessException("上级菜单不能选择自身");
        }
        Set<Long> descendantIds = listDescendantIdsById(menuUpdateDTO.getId());
        if (descendantIds.contains(menuUpdateDTO.getPid())) {
            throw new BusinessException("上级菜单不能选择其下级菜单");
        }

        SysMenu entity = menuConverter.toEntity(menuUpdateDTO);
        updateById(entity);
        evictAllCaches();
    }

    /**
     * 修改菜单状态。
     * <p>
     * 系统内置菜单不允许禁用(启用请求不受限);同状态幂等返回;
     * 菜单状态影响权限码与路由装配,失效全部用户的 perm/menu 缓存。
     *
     * @param menuChangeStatusDTO 菜单状态信息
     */
    @Override
    public void changeStatus(MenuChangeStatusDTO menuChangeStatusDTO) {
        SysMenu sysMenu = getById(menuChangeStatusDTO.getId());
        if (ObjUtil.isNull(sysMenu)) {
            throw new BusinessException("菜单不存在");
        }

        // 系统内置菜单不允许禁用(启用请求不受限)
        if (BuiltinEnum.YES.getCode().equals(sysMenu.getBuiltin())
                && EnableEnum.DISABLE.getCode().equals(menuChangeStatusDTO.getStatus())) {
            throw new BusinessException("系统内置菜单不允许禁用");
        }

        // 状态一致时幂等返回
        if (menuChangeStatusDTO.getStatus().equals(sysMenu.getStatus())) {
            return;
        }

        SysMenu entity = new SysMenu();
        entity.setId(menuChangeStatusDTO.getId());
        entity.setStatus(menuChangeStatusDTO.getStatus());
        updateById(entity);
        evictAllCaches();
    }

    /**
     * 删除菜单。
     * <p>
     * 系统内置菜单不允许删除;级联删除其全部下级菜单并清理角色-菜单关联;
     * 缓存失效注册到事务提交后执行,避免提交前其他请求回源把中间状态重新写回缓存。
     *
     * @param id 菜单ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        SysMenu sysMenu = getById(id);
        if (ObjUtil.isNull(sysMenu)) {
            throw new BusinessException("菜单不存在");
        }

        // 系统内置菜单不允许删除
        if (BuiltinEnum.YES.getCode().equals(sysMenu.getBuiltin())) {
            throw new BusinessException("系统内置菜单不允许删除");
        }

        // 级联删除全部下级菜单
        Set<Long> descendantIds = listDescendantIdsById(id);
        List<Long> deleteIds = new ArrayList<>(descendantIds);
        deleteIds.add(id);
        removeByIds(deleteIds);

        // 清理角色-菜单关联
        sysRoleMenuService.remove(SYS_ROLE_MENU.MENU_ID.in(deleteIds));

        evictAllCaches();
    }

    /**
     * 校验上级菜单合法性:pid 为 0 表示根节点;否则父级必须存在且不能为按钮类型。
     *
     * @param pid 上级菜单ID
     */
    private void validatePid(Long pid) {
        if (AppConst.ZERO_LONG.equals(pid)) {
            return;
        }
        SysMenu parent = getById(pid);
        if (ObjUtil.isNull(parent)) {
            throw new BusinessException("上级菜单不存在");
        }
        if (MenuType.B.getCode().equals(parent.getType())) {
            throw new BusinessException("按钮类型菜单下不允许创建子菜单");
        }
    }

    /**
     * 校验按菜单类型的必填项:目录/菜单必须填写路由路径,按钮必须填写权限编码。
     *
     * @param type  菜单类型
     * @param path  路由路径
     * @param perms 权限编码
     */
    private void validateTypeRequired(String type, String path, String perms) {
        if (MenuType.B.getCode().equals(type) && StrUtil.isBlank(perms)) {
            throw new BusinessException("按钮类型菜单必须设置权限编码");
        }
        if (!MenuType.B.getCode().equals(type) && StrUtil.isBlank(path)) {
            throw new BusinessException("目录/菜单类型必须填写路由路径");
        }
    }

    /**
     * 校验同级下菜单名称唯一。
     *
     * @param pid       父级菜单ID
     * @param title     菜单名称
     * @param excludeId 修改时排除自身,新增时传 null
     */
    private void validateSiblingTitleUnique(Long pid, String title, Long excludeId) {
        QueryChain<SysMenu> query = QueryChain.of(sysMenuMapper)
                .where(SYS_MENU.PID.eq(pid))
                .and(SYS_MENU.TITLE.eq(title));
        if (excludeId != null) {
            query.and(SYS_MENU.ID.ne(excludeId));
        }
        if (query.count() > 0) {
            throw new BusinessException("同级下已存在同名菜单");
        }
    }

    /**
     * 校验权限标识全局唯一(空标识跳过,仅按钮类型填写)。
     *
     * @param perms     权限编码
     * @param excludeId 修改时排除自身,新增时传 null
     */
    private void validatePermsUnique(String perms, Long excludeId) {
        if (StrUtil.isBlank(perms)) {
            return;
        }
        QueryChain<SysMenu> query = QueryChain.of(sysMenuMapper)
                .where(SYS_MENU.PERMS.eq(perms));
        if (excludeId != null) {
            query.and(SYS_MENU.ID.ne(excludeId));
        }
        if (query.count() > 0) {
            throw new BusinessException("权限标识已存在");
        }
    }

    /**
     * 校验系统内置菜单的结构字段未被修改。
     * <p>
     * 内置菜单的上级、路由路径、组件路径与权限编码和源码路由、前后端权限契约硬编码绑定,
     * 修改会直接导致功能入口失效或权限判定错位;空串与 null 视为一致,
     * 避免表单提交空串被误判为修改。
     *
     * @param sysMenu       数据库中的内置菜单
     * @param menuUpdateDTO 修改请求参数
     */
    private void validateBuiltinUnchanged(SysMenu sysMenu, MenuUpdateDTO menuUpdateDTO) {
        if (!sysMenu.getPid().equals(menuUpdateDTO.getPid())) {
            throw new BusinessException("系统内置菜单不允许修改上级菜单");
        }
        if (!StrUtil.equals(StrUtil.emptyIfNull(sysMenu.getPath()), menuUpdateDTO.getPath())) {
            throw new BusinessException("系统内置菜单不允许修改路由路径");
        }
        if (!StrUtil.equals(StrUtil.emptyIfNull(sysMenu.getComponent()), menuUpdateDTO.getComponent())) {
            throw new BusinessException("系统内置菜单不允许修改组件路径");
        }
        if (!StrUtil.equals(StrUtil.emptyIfNull(sysMenu.getPerms()), menuUpdateDTO.getPerms())) {
            throw new BusinessException("系统内置菜单不允许修改权限编码");
        }
    }

    /**
     * 收集指定菜单的全部下级菜单ID(不含自身)。
     * <p>
     * 菜单表数据量小,一次性加载后沿 pid 向下遍历;visited 兼作防环终止条件,
     * 规避 pid 环脏数据导致的死循环。
     *
     * @param menuId 菜单ID
     * @return 全部下级菜单ID集合
     */
    private Set<Long> listDescendantIdsById(Long menuId) {
        Map<Long, SysMenu> menuMap = QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .list()
                .stream()
                .collect(Collectors.toMap(SysMenu::getId, Function.identity(), (a, b) -> a));

        Set<Long> descendantIds = new HashSet<>();
        collectDescendantIds(menuId, menuMap, descendantIds);
        return descendantIds;
    }

    /**
     * 沿 pid 向下递归收集 menuId 的全部下级菜单ID(不含自身)。
     *
     * @param menuId        菜单ID
     * @param menuMap       id -> 菜单 映射
     * @param descendantIds 已收集的下级ID集合(防环:同一 ID 仅收集一次)
     */
    private void collectDescendantIds(Long menuId, Map<Long, SysMenu> menuMap, Set<Long> descendantIds) {
        for (SysMenu menu : menuMap.values()) {
            if (menuId.equals(menu.getPid()) && descendantIds.add(menu.getId())) {
                collectDescendantIds(menu.getId(), menuMap, descendantIds);
            }
        }
    }

    /**
     * 失效全部用户的 perm/menu 缓存。
     * <p>
     * 菜单数据被所有用户共享,无法按用户 Key 精准失效,直接清空两个缓存;
     * 注册到事务提交后执行,避免提交前其他请求回源把中间状态重新写回缓存。
     */
    private void evictAllCaches() {
        CacheUtil.evictAfterCommit(() -> {
            Cache permCache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.PERM));
            if (permCache != null) {
                permCache.clear();
            }
            Cache menuCache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.MENU));
            if (menuCache != null) {
                menuCache.clear();
            }
        });
    }

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
            log.debug("perm 缓存未注册，降级为直接查库：userId={}", userId);
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
            log.debug("menu 缓存未注册，降级为直接查库：userId={}", userId);
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
     * 批量获取多个菜单的所有祖先ID（并集，去重）。
     * <p>
     * 菜单表数据量小，一次性加载后在内存中沿 pid 向上遍历，将原来 M×深度 次的逐层点查优化为 1 条 SQL。
     */
    @Override
    public Set<Long> listAncestorIdsByIds(List<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return Collections.emptySet();
        }
        // 一次加载全部菜单，构建 id -> 菜单 映射
        Map<Long, SysMenu> menuMap = QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .list()
                .stream()
                .collect(Collectors.toMap(SysMenu::getId, Function.identity(), (a, b) -> a));

        Set<Long> ancestorIds = new HashSet<>();
        for (Long menuId : menuIds) {
            collectAncestorIds(menuId, menuMap, ancestorIds);
        }
        return ancestorIds;
    }

    /**
     * 沿 pid 向上遍历，收集 menuId 自身与所有祖先ID。
     * <p>
     * visited 的 add 返回值兼作防环终止条件：同一 ID 第二次出现（pid 环脏数据）时立即终止，
     * 避免死循环；pid 指向的菜单不存在时正常结束。
     *
     * @param menuId  起始菜单ID
     * @param menuMap id -> 菜单 映射
     * @param visited 已访问ID集合（跨菜单共享，保证并集去重）
     */
    private void collectAncestorIds(Long menuId, Map<Long, SysMenu> menuMap, Set<Long> visited) {
        Long current = menuId;
        while (current != null && !AppConst.ZERO_LONG.equals(current) && visited.add(current)) {
            SysMenu menu = menuMap.get(current);
            if (menu == null) {
                break;
            }
            current = menu.getPid();
        }
    }

}
