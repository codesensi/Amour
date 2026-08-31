package cn.codesensi.amour.service;

import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysUserRole;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户角色关联表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 返回一个账号所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> listRoleByUserId(Long userId);

    /**
     * 返回一个账号所拥有的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> listRoleCodeByUserId(Long userId);

    /**
     * 失效指定用户的角色编码缓存（role 缓存）
     *
     * @param userIds 用户ID列表
     */
    void evictRoleCache(List<Long> userIds);

}
