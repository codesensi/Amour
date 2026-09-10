package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.enums.FileBizTypeEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.converter.MenuConverter;
import cn.codesensi.amour.model.converter.UserConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.entity.SysUserRole;
import cn.codesensi.amour.service.*;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;

/**
 * 用户信息 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 *
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysMenuService sysMenuService;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final UserConverter userConverter;
    private final MenuConverter menuConverter;
    private final SysUserRoleService sysUserRoleService;
    private final CacheManager cacheManager;
    private final CacheEvictService cacheEvictService;
    private final FileService fileService;

    /**
     * 分页查询用户信息。
     * <p>
     * 用户名称、手机号为模糊匹配，状态为精确匹配，条件缺省时自动忽略；
     * 页码与每页条数的缺省值由 {@link BasePage} 提供(1 与 20)，与前端默认值保持一致。
     *
     * @param userPageDTO 分页查询参数
     * @return 用户信息分页结果
     */
    @Override
    public Page<SysUser> page(UserPageDTO userPageDTO) {
        return QueryChain.of(sysUserMapper)
                .select(SYS_USER.ALL_COLUMNS)
                .where(SYS_USER.USERNAME.like(userPageDTO.getUsername(), StrUtil::isNotBlank))
                .and(SYS_USER.NICKNAME.like(userPageDTO.getNickname(), StrUtil::isNotBlank))
                .and(SYS_USER.ID_CARD.like(userPageDTO.getIdCard(), StrUtil::isNotBlank))
                .and(SYS_USER.PHONE.like(userPageDTO.getPhone(), StrUtil::isNotBlank))
                .and(SYS_USER.QQ.like(userPageDTO.getQq(), StrUtil::isNotBlank))
                .and(SYS_USER.EMAIL.like(userPageDTO.getEmail(), StrUtil::isNotBlank))
                .and(SYS_USER.GENDER.eq(userPageDTO.getGender(), StrUtil::isNotBlank))
                .and(SYS_USER.STATUS.eq(userPageDTO.getStatus(), ObjUtil::isNotNull))
                .page(Page.of(userPageDTO.getPageNumber(), userPageDTO.getPageSize()));
    }

    /**
     * 获取当前用户信息。
     * <p>
     * 资料部分经 userInfo 缓存加速（Key 为用户ID），写后 30 天兜底过期，写侧显式失效，
     * 缓存未注册/未就绪时降级为直接查库；角色/权限/菜单不进该缓存，每次实时装配——
     * 它们各自拥有独立缓存与失效路径，避免会话维度数据被烤进资料快照后因漏失效而不一致。
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserInfoDTO getCurrentUser(Long userId) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.USER));
        // 资料部分走 user 缓存（仅 DB 维度的用户资料，不含角色/权限/菜单）
        UserInfoDTO userInfoDTO;
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            log.debug("userInfo 缓存未注册，降级为直接查库：userId={}", userId);
            userInfoDTO = loadUserProfile(userId);
        } else {
            userInfoDTO = cache.get(userId, () -> loadUserProfile(userId));
        }
        if (ObjUtil.isNotNull(userInfoDTO)) {
            // 角色与权限：实时装配（各自有 role/perm 缓存加速；StpInterfaceImpl 即转发至这两个方法，行为与 StpUtil 等价）
            userInfoDTO.setRoles(sysUserRoleService.listRoleCodeByUserId(userId));
            userInfoDTO.setPerms(sysMenuService.listPermCodeByUserId(userId));
            // 拥有的菜单（menu 缓存加速）
            List<SysMenu> menus = sysMenuService.listMenuByUserId(userId);
            userInfoDTO.setMenus(menuConverter.toListDTO(menus));
        }
        return userInfoDTO;
    }

    /**
     * 从库中加载用户资料（仅 DB 维度字段，不含角色/权限/菜单等聚合维度数据）。
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    private UserInfoDTO loadUserProfile(Long userId) {
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .select(SYS_USER.ALL_COLUMNS)
                .where(SYS_USER.ID.eq(userId))
                .one();
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        return userConverter.toInfoDTO(sysUser);
    }

    /**
     * 新增用户信息
     *
     * @param userInsertDTO 用户信息
     */
    @Override
    public void insert(UserInsertDTO userInsertDTO) {
        String username = userInsertDTO.getUsername();
        // 校验用户名是否存在
        long count = QueryChain.of(sysUserMapper)
                .where(SYS_USER.USERNAME.eq(username))
                .count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 状态缺省视为启用(0,与 EnableEnum 对齐)
        if (ObjUtil.isNull(userInsertDTO.getStatus())) {
            userInsertDTO.setStatus(EnableEnum.ENABLE.getCode());
        }

        SysUser sysUser = userConverter.toEntity(userInsertDTO);
        // 若未输入昵称则保持昵称和用户名相同
        if (StrUtil.isBlank(userInsertDTO.getNickname())) {
            log.debug("未输入昵称，默认与用户名一致：username={}", username);
            sysUser.setNickname(username);
        }

        // 默认密码
        log.debug("使用系统默认密码初始化用户：username={}", username);
        String password = BCrypt.hashpw(AppConst.DEFAULT_PASSWORD, BCrypt.gensalt());
        sysUser.setPassword(password);
        sysUserMapper.insert(sysUser, true);

        // 头像采纳:回填文件业务归属并标记被替换的旧头像失效(外链等非本系统地址自动跳过)
        if (StrUtil.isNotBlank(sysUser.getAvatar())) {
            fileService.bindBizFiles(FileBizTypeEnum.AVATAR, sysUser.getId(), List.of(sysUser.getAvatar()));
        }
    }

    /**
     * 修改用户信息。
     * <p>
     * 仅更新请求中的资料字段（用户名、状态、密码不在可修改范围），
     * MyBatis-Flex 按忽略 null 策略更新；更新成功后复用 {@link #updateById(SysUser)}
     * 的缓存失效逻辑。
     *
     * @param userUpdateDTO 用户信息
     */
    @Override
    public void update(UserUpdateDTO userUpdateDTO) {
        SysUser sysUser = getById(userUpdateDTO.getId());
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        SysUser entity = userConverter.toEntity(userUpdateDTO);
        updateById(entity);

        // 头像采纳:回填文件业务归属并标记被替换的旧头像失效(外链等非本系统地址自动跳过)
        if (StrUtil.isNotBlank(entity.getAvatar())) {
            fileService.bindBizFiles(FileBizTypeEnum.AVATAR, entity.getId(), List.of(entity.getAvatar()));
        }
    }

    /**
     * 修改用户状态。
     * <p>
     * 仅更新 status 字段；系统内置用户不允许停用；状态一致时幂等返回；
     * 更新成功后复用 {@link #updateById(SysUser)} 的缓存失效逻辑。
     *
     * @param userChangeStatusDTO 状态信息
     */
    @Override
    public void changeStatus(UserChangeStatusDTO userChangeStatusDTO) {
        SysUser sysUser = getById(userChangeStatusDTO.getId());
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }

        // 系统内置用户不允许禁用(启用请求不受限)
        if (BuiltinEnum.YES.getCode().equals(sysUser.getBuiltin())
                && EnableEnum.DISABLE.getCode().equals(userChangeStatusDTO.getStatus())) {
            throw new BusinessException("系统内置用户不允许禁用");
        }

        // 状态一致时幂等返回
        if (userChangeStatusDTO.getStatus().equals(sysUser.getStatus())) {
            return;
        }

        SysUser entity = new SysUser();
        entity.setId(userChangeStatusDTO.getId());
        entity.setStatus(userChangeStatusDTO.getStatus());
        updateById(entity);

        // 禁用即踢出该用户当前会话,避免已签发令牌继续有效(启用无需处理)
        if (EnableEnum.DISABLE.getCode().equals(userChangeStatusDTO.getStatus())) {
            StpUtil.logout(userChangeStatusDTO.getId());
        }
    }

    /**
     * 删除用户信息。
     * <p>
     * 支持单个或批量删除，批量与单个共用同一校验链；逻辑删除由 MyBatis-Flex 全局
     * del_flag 配置自动完成；删除用户与清理角色关联处于同一事务，原子提交，
     * 任一校验不通过即整批回滚，不做部分成功；缓存失效注册在事务提交后执行，
     * 避免提交前其他请求回源查库把中间状态重新写入缓存。
     *
     * @param ids 用户ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }

        // 1. 校验用户是否存在（逻辑删除的用户视为不存在）
        List<SysUser> sysUsers = listByIds(ids);
        List<Long> existingIds = sysUsers.stream().map(SysUser::getId).toList();
        List<Long> missingIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();
        if (CollUtil.isNotEmpty(missingIds)) {
            throw new BusinessException("用户不存在：" + missingIds);
        }

        // 2. 系统内置用户不允许删除（整批回滚）
        boolean containsBuiltin = sysUsers.stream()
                .anyMatch(user -> BuiltinEnum.YES.getCode().equals(user.getBuiltin()));
        if (containsBuiltin) {
            throw new BusinessException("系统内置用户不允许删除");
        }

        // 3. 不允许删除当前登录用户，避免误删自己导致无法管理（整批回滚）
        if (ids.contains(StpUtil.getLoginIdAsLong())) {
            throw new BusinessException("不允许删除当前登录用户");
        }

        // 4. 逻辑删除用户并清理角色关联
        removeByIds(ids);
        sysUserRoleService.remove(SYS_USER_ROLE.USER_ID.in(ids));

        // 5. 失效这些用户的角色/权限/路由菜单/用户信息缓存（注册到事务提交后执行）
        CacheUtil.evictAfterCommit(() -> {
            log.debug("用户删除完成，失效缓存：userIds={}", ids);
            cacheEvictService.evictRoleCache(ids);
            cacheEvictService.evictPermCache(ids);
            cacheEvictService.evictMenuCache(ids);
            cacheEvictService.evictUserCache(ids);
            // 已删除用户立即下线,避免已签发会话继续有效
            ids.forEach(StpUtil::logout);
        });
    }

    /**
     * 重置用户密码为系统默认密码({@link AppConst#DEFAULT_PASSWORD})。
     * <p>
     * BCrypt 加密后仅更新 password 字段，更新成功后复用
     * {@link #updateById(SysUser)} 的缓存失效逻辑。
     *
     * @param id 用户ID
     */
    @Override
    public void resetPassword(Long id) {
        SysUser sysUser = getById(id);
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        SysUser entity = new SysUser();
        entity.setId(id);
        entity.setPassword(BCrypt.hashpw(AppConst.DEFAULT_PASSWORD, BCrypt.gensalt()));
        updateById(entity);
    }

    /**
     * 配置用户角色。
     * <p>
     * 删除旧关联与写入新关联处于同一事务，原子提交，避免中途失败留下"旧关联已删、新关联未插"的半状态；
     * 缓存失效注册在事务提交后执行，避免提交前其他请求回源查库把中间状态重新写入缓存。
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

        // 3. 角色ID去重并校验是否存在（避免产生悬空关联）
        List<Long> roleIds = assignRolesDTO.getRoleIds();
        List<Long> distinctRoleIds = CollUtil.isEmpty(roleIds)
                ? List.of()
                : roleIds.stream().distinct().toList();
        if (CollUtil.isNotEmpty(distinctRoleIds)) {
            checkRolesExist(distinctRoleIds);
        }

        // 4. 删除旧关联
        sysUserRoleService.remove(SYS_USER_ROLE.USER_ID.eq(userId));

        // 5. 插入新关联（如果角色列表为空，则仅删除）
        if (CollUtil.isNotEmpty(distinctRoleIds)) {
            List<SysUserRole> entities = distinctRoleIds.stream()
                    .map(roleId -> {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setRoleId(roleId);
                        return sysUserRole;
                    }).toList();
            // 批量插入
            sysUserRoleService.saveBatch(entities);
        }

        // 6. 失效该用户的角色/权限/路由菜单/用户信息缓存（角色变更必然影响权限码与可访问菜单）；
        //    注册到事务提交后执行，避免提交前其他请求回源查库把中间状态重新写入缓存
        CacheUtil.evictAfterCommit(() -> {
            log.debug("角色分配完成，失效缓存：userId={}，roleIds={}", userId, distinctRoleIds);
            cacheEvictService.evictRoleCache(List.of(userId));
            cacheEvictService.evictPermCache(List.of(userId));
            cacheEvictService.evictMenuCache(List.of(userId));
            cacheEvictService.evictUserCache(List.of(userId));
        });
    }

    /**
     * 查询用户已分配的角色ID列表。
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        return sysUserRoleService.listRoleIdsByUserId(userId);
    }

    /**
     * 校验待分配的角色是否都存在（逻辑删除的角色视为不存在），避免产生悬空关联。
     *
     * @param roleIds 去重后的角色ID列表
     */
    private void checkRolesExist(List<Long> roleIds) {
        List<Long> existingIds = QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ID)
                .where(SYS_ROLE.ID.in(roleIds))
                .listAs(Long.class);
        List<Long> missingIds = roleIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();
        if (CollUtil.isNotEmpty(missingIds)) {
            throw new BusinessException("角色不存在：" + missingIds);
        }
    }

    /**
     * 更新用户信息。
     * <p>
     * 昵称、头像等资料变更影响用户信息聚合体，失效该用户的 userInfo 缓存。
     */
    @Override
    public boolean updateById(@NonNull SysUser sysUser) {
        boolean success = super.updateById(sysUser);
        if (success) {
            log.debug("用户信息更新成功，失效 userInfo 缓存：userId={}", sysUser.getId());
            cacheEvictService.evictUserCache(List.of(sysUser.getId()));
        }
        return success;
    }

}