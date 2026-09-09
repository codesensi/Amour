package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.MenuChangeStatusDTO;
import cn.codesensi.amour.model.dto.MenuInsertDTO;
import cn.codesensi.amour.model.dto.MenuUpdateDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 路由菜单表 服务层。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 新增菜单
     *
     * @param menuInsertDTO 菜单信息
     */
    void insert(MenuInsertDTO menuInsertDTO);

    /**
     * 修改菜单
     *
     * @param menuUpdateDTO 菜单信息
     */
    void update(MenuUpdateDTO menuUpdateDTO);

    /**
     * 修改菜单状态
     *
     * @param menuChangeStatusDTO 菜单状态信息
     */
    void changeStatus(MenuChangeStatusDTO menuChangeStatusDTO);

    /**
     * 删除菜单(级联删除其全部下级菜单，并清理角色-菜单关联)
     *
     * @param id 菜单ID
     */
    void delete(Long id);

    /**
     * 返回一个账号所拥有的权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> listPermCodeByUserId(Long userId);

    /**
     * 查询用户路由菜单列表
     *
     * @param userId 用户id
     * @return 路由菜单列表
     */
    List<SysMenu> listMenuByUserId(Long userId);

    /**
     * 批量获取多个菜单的所有祖先ID（并集，去重）
     *
     * @param menuIds 菜单ID列表
     * @return 菜单的所有祖先ID（包含自身）
     */
    Set<Long> listAncestorIdsByIds(List<Long> menuIds);

}