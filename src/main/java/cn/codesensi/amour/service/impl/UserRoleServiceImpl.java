package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.model.entity.SysUserRole;
import cn.codesensi.amour.mapper.SysUserRoleMapper;
import cn.codesensi.amour.service.UserRoleService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements UserRoleService {
}
