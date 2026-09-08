package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysRole;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 角色信息表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色信息
     *
     * @param rolePageDTO 分页查询参数
     * @return 角色信息分页结果
     */
    Page<SysRole> page(RolePageDTO rolePageDTO);

    /**
     * 新增角色信息
     *
     * @param roleInsertDTO 角色信息
     */
    void insert(RoleInsertDTO roleInsertDTO);

    /**
     * 修改角色信息
     *
     * @param roleUpdateDTO 角色信息
     */
    void update(RoleUpdateDTO roleUpdateDTO);

    /**
     * 修改角色状态
     *
     * @param roleChangeStatusDTO 角色状态信息
     */
    void changeStatus(RoleChangeStatusDTO roleChangeStatusDTO);

    /**
     * 批量删除角色信息
     *
     * @param ids 角色ID列表
     */
    void delete(List<Long> ids);

    /**
     * 分配角色菜单权限
     *
     * @param assignMenusDTO 角色菜单权限信息
     */
    void assignMenus(AssignMenusDTO assignMenusDTO);

    /**
     * 查询角色已分配的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> listMenuIdsByRoleId(Long roleId);
}
