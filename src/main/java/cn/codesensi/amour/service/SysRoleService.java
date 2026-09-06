package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.AssignMenusDTO;
import cn.codesensi.amour.model.dto.RoleInsertDTO;
import cn.codesensi.amour.model.entity.SysRole;
import com.mybatisflex.core.service.IService;

/**
 * 角色信息表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 新增角色信息
     *
     * @param roleInsertDTO 角色信息
     */
    void insert(RoleInsertDTO roleInsertDTO);

    /**
     * 分配角色菜单权限
     *
     * @param assignMenusDTO 角色菜单权限信息
     */
    void assignMenus(AssignMenusDTO assignMenusDTO);
}
