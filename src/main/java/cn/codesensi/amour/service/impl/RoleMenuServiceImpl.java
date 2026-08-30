package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.model.entity.SysRoleMenu;
import cn.codesensi.amour.mapper.SysRoleMenuMapper;
import cn.codesensi.amour.service.RoleMenuService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色菜单关联 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements RoleMenuService {
}
