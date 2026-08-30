package cn.codesensi.amour.service;

import cn.codesensi.amour.model.entity.SysUserRole;
import com.mybatisflex.core.service.IService;

/**
 * 用户角色关联 Service。
 * <p>
 * 继承 MyBatis-Flex 的 {@link IService}，开箱即得 {@code sys_user_role} 表的增删改查能力；
 * 业务方法按需在后续迭代中补充。
 */
public interface UserRoleService extends IService<SysUserRole> {
}
