package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户信息 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 */
@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
}
