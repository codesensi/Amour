package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.converter.SysUserConverter;
import cn.codesensi.amour.model.dto.AssignRolesDTO;
import cn.codesensi.amour.model.dto.MenuDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserSaveDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.entity.SysUserRole;
import cn.codesensi.amour.service.SysMenuService;
import cn.codesensi.amour.service.SysUserService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserInfoDTO getCurrentUser(Long userId) {
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

        // 默认随机头像
        if (StrUtil.isBlank(userSaveDTO.getAvatar())) {
            // TODO 取系统配置
            // String avatar = String.format(appProperties.getAvatar(), username);
            // sysUser.setAvatar(avatar);
        }

        // 默认密码
        String password = BCrypt.hashpw(AppConst.DEFAULT_PASSWORD, BCrypt.gensalt());
        sysUser.setPassword(password);
        sysUserMapper.insert(sysUser, true);
    }

    /**
     * 配置用户角色
     *
     * @param assignRolesDTO 分配角色信息
     */
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
        // TODO 同步清除用户的相关缓存（权限、菜单、用户信息）

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
}
