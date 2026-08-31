package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.converter.SysUserConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.entity.SysUserRole;
import cn.codesensi.amour.service.SysConfigService;
import cn.codesensi.amour.service.SysMenuService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.codesensi.amour.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;

/**
 * 用户信息 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 */
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysMenuService sysMenuService;
    private final SysUserMapper sysUserMapper;
    private final SysUserConverter sysUserConverter;
    private final SysUserRoleService sysUserRoleService;
    private final SysConfigService sysConfigService;
    private final CacheManager cacheManager;

    /**
     * 获取当前用户信息。
     * <p>
     * 结果经 userInfo 缓存加速（Key 为用户ID），写后 30 天兜底过期，写侧显式失效；
     * 缓存未注册/未就绪时降级为直接查库。
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserInfoDTO getCurrentUser(Long userId) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.USER));
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            return loadCurrentUser(userId);
        }
        // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿
        return cache.get(userId, () -> loadCurrentUser(userId));
    }

    /**
     * 从库中组装当前用户信息（资料 + 角色 + 权限 + 菜单）。
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    private UserInfoDTO loadCurrentUser(Long userId) {
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .select(SYS_USER.ALL_COLUMNS)
                .where(SYS_USER.ID.eq(userId))
                .one();
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        UserInfoDTO userInfoDTO = sysUserConverter.toUserInfoDTO(sysUser);
        // 角色集合
        List<String> roles = StpUtil.getRoleList();
        userInfoDTO.setRoles(roles);
        // 权限码集合
        List<String> perms = StpUtil.getPermissionList();
        userInfoDTO.setPerms(perms);
        // 拥有的菜单
        List<SysMenu> menus = sysMenuService.listMenuByUserId(userId);
        List<MenuDTO> menuDTOS = sysUserConverter.toMenuDTOList(menus);
        userInfoDTO.setMenus(menuDTOS);
        return userInfoDTO;
    }

    /**
     * 保存用户信息
     *
     * @param userSaveDTO 用户信息
     */
    @Override
    public void saveUser(UserSaveDTO userSaveDTO) {
        String username = userSaveDTO.getUsername();
        // 校验用户名是否存在
        long count = QueryChain.of(sysUserMapper)
                .where(SYS_USER.USERNAME.eq(username))
                .count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser sysUser = sysUserConverter.toEntity(userSaveDTO);
        // 若未输入昵称则保持昵称和用户名相同
        if (StrUtil.isBlank(userSaveDTO.getNickname())) {
            sysUser.setNickname(username);
        }

        // 默认随机头像：未上传头像时读取系统配置的随机头像服务地址，以用户名作为随机种子生成
        if (StrUtil.isBlank(userSaveDTO.getAvatar())) {
            // 配置缺失/停用时保持头像为空，避免因缺配置导致保存失败
            String avatarTemplate = sysConfigService.listByKeys(List.of(ConfigKeyEnum.AVATAR.getCode()))
                    .stream().findFirst()
                    .map(ConfigDTO::getConfigValue)
                    .orElse(null);
            if (StrUtil.isNotBlank(avatarTemplate)) {
                sysUser.setAvatar(String.format(avatarTemplate, username));
            }
        }

        // 默认密码
        String password = BCrypt.hashpw(AppConst.DEFAULT_PASSWORD, BCrypt.gensalt());
        sysUser.setPassword(password);
        sysUserMapper.insert(sysUser, true);
    }

    /**
     * 配置用户角色。
     * <p>
     * 删除旧关联与写入新关联处于同一事务，原子提交，避免中途失败留下"旧关联已删、新关联未插"的半状态。
     *
     * @param assignRolesDTO 分配角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignRoles(AssignRolesDTO assignRolesDTO) {
        Long userId = assignRolesDTO.getUserId();
        // 1. 校验用户是否存在
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .select(SYS_USER.BUILTIN)
                .where(SYS_USER.ID.eq(userId))
                .one();
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }

        // 2. 系统内置用户不允许修改角色
        if (BuiltinEnum.YES.getCode().equals(sysUser.getBuiltin())) {
            throw new BusinessException("系统内置用户不允许修改角色");
        }

        // 3. 删除旧关联
        sysUserRoleService.remove(SYS_USER_ROLE.USER_ID.eq(userId));
        // 4. 失效该用户的角色/权限/路由菜单/用户信息缓存（角色变更必然影响权限码与可访问菜单）
        sysUserRoleService.evictRoleCache(userId);
        sysMenuService.evictPermCache(List.of(userId));
        sysMenuService.evictMenuCache(List.of(userId));
        evictUserCache(userId);

        List<Long> roleIds = assignRolesDTO.getRoleIds();
        // 如果角色列表为空，则仅删除旧关联
        if (CollUtil.isNotEmpty(roleIds)) {
            // 角色ID去重
            roleIds = roleIds.stream().distinct().toList();
            // 4. 插入新关联（如果角色列表为空，则仅删除）
            List<SysUserRole> entities = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setRoleId(roleId);
                        return sysUserRole;
                    }).toList();
            // 批量插入
            sysUserRoleService.saveBatch(entities);
        }
    }

    /**
     * 更新用户信息。
     * <p>
     * 昵称、头像等资料变更影响用户信息聚合体，失效该用户的 userInfo 缓存。
     */
    @Override
    public boolean updateById(SysUser sysUser) {
        boolean success = super.updateById(sysUser);
        if (success) {
            evictUserCache(sysUser.getId());
        }
        return success;
    }

    /**
     * 失效指定用户的用户信息缓存。
     *
     * @param userId 用户ID
     */
    @Override
    public void evictUserCache(Long userId) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.USER));
        if (cache != null) {
            cache.evict(userId);
        }
    }

}
