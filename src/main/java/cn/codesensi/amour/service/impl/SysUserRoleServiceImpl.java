package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.mapper.SysUserRoleMapper;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysUserRole;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;


/**
 * 用户角色关联表 服务层实现。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final CacheManager cacheManager;

    /**
     * 返回一个账号所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> listRoleByUserId(Long userId) {
        return QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ALL_COLUMNS)
                .leftJoin(SYS_USER_ROLE).on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .list();
    }

    /**
     * 返回一个账号所拥有的角色编码列表。
     * <p>
     * 结果经 role 缓存加速（Key 为用户ID），写后 30 天兜底过期，写侧显式失效；
     * 缓存未注册/未就绪时降级为直接查库。
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Override
    public List<String> listRoleCodeByUserId(Long userId) {
        // 统一缓存读取：原子回源 + 缓存故障降级（见 CacheUtil#load）
        return CacheUtil.load(cacheManager, CacheConst.ROLE, userId, this::loadRoleCodes);
    }

    /**
     * 从库中加载角色编码列表（去重，超级管理员短路）。
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    private List<String> loadRoleCodes(Long userId) {
        // 获取去重后的角色列表
        List<SysRole> sysRoles = listRoleByUserId(userId);
        // 获取角色编码列表
        List<String> roleCodeList = sysRoles.stream()
                .map(SysRole::getCode)
                .filter(StrUtil::isNotBlank)
                .toList();
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }
        // 超级管理员角色编码
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return List.of(RbacConst.ROLE_ADMIN_CODE);
        }
        return roleCodeList;
    }

    /**
     * 查询用户已分配的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        return queryChain()
                .select(SYS_USER_ROLE.ROLE_ID)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .listAs(Long.class);
    }

}
