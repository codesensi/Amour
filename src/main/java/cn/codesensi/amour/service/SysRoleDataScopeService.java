package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.RoleDataScopeDTO;
import cn.codesensi.amour.model.dto.RoleDataScopeSaveDTO;

import java.util.List;

/**
 * 角色数据范围策略服务 —— 数据权限配置的查询与保存。
 * <p>
 * 超级管理员角色（code=admin）数据范围由判定层硬编码为 all/all，
 * 不参与本服务的配置（查询标记 superAdmin、保存直接拒绝）。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysRoleDataScopeService {

    /**
     * 查询角色的全模块数据范围配置。
     * <p>
     * 无配置行的模块按最小权限兜底 self 下发；超级管理员角色整单标记 superAdmin
     * 并以 all/all 展示（仅展示口径，配置不落库）。
     *
     * @param roleId 角色ID
     * @return 全模块的范围档位配置列表
     */
    List<RoleDataScopeDTO> listByRoleId(Long roleId);

    /**
     * 保存角色的全模块数据范围配置（整角色覆盖式保存）。
     * <p>
     * 校验角色存在且非超级管理员、模块键合法、可改范围不宽于可见范围；
     * 保存成功后失效该角色的 data-scope 缓存，判定即时生效。
     *
     * @param saveDTO 保存参数
     */
    void assignDataScope(RoleDataScopeSaveDTO saveDTO);
}
