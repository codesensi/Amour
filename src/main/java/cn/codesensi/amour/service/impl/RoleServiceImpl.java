package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.service.RoleService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色信息 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 */
@Service
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {
}
