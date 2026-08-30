package cn.codesensi.amour.security;

import cn.codesensi.amour.service.SysMenuService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Service
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysMenuService sysMenuService;
    private final SysUserRoleService sysUserRoleService;

    /**
     * 返回一个账号所拥有的权限编码列表
     *
     * @param loginId   用户ID
     * @param loginType 登录设备
     * @return 权限编码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String userIdStr = String.valueOf(loginId);
        Long userId = Long.valueOf(userIdStr);
        return sysMenuService.listPermCodeByUserId(userId);
    }

    /**
     * 返回一个账号所拥有的角色编码列表
     *
     * @param loginId   用户ID
     * @param loginType 登录设备
     * @return 角色编码列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String userIdStr = String.valueOf(loginId);
        Long userId = Long.valueOf(userIdStr);
        return sysUserRoleService.listRoleCodeByUserId(userId);
    }

}
