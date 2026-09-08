package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.AssignRolesDTO;
import cn.codesensi.amour.model.dto.UserChangeStatusDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserPageDTO;
import cn.codesensi.amour.model.dto.UserInsertDTO;
import cn.codesensi.amour.model.dto.UserUpdateDTO;
import cn.codesensi.amour.model.entity.SysUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户信息 Service。
 * <p>
 * 继承 MyBatis-Flex 的 {@link IService}，开箱即得 {@code sys_user} 表的增删改查能力；
 * 业务方法按需在后续迭代中补充。
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户信息
     *
     * @param userPageDTO 分页查询参数
     * @return 用户信息分页结果
     */
    Page<SysUser> page(UserPageDTO userPageDTO);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoDTO getCurrentUser(Long userId);

    /**
     * 新增用户信息
     *
     * @param userInsertDTO 用户信息
     */
    void insert(UserInsertDTO userInsertDTO);

    /**
     * 修改用户信息(仅资料字段,用户名、状态与密码不在可修改范围)
     *
     * @param userUpdateDTO 用户信息
     */
    void update(UserUpdateDTO userUpdateDTO);

    /**
     * 修改用户状态(系统内置用户不允许停用)
     *
     * @param userChangeStatusDTO 状态信息
     */
    void changeStatus(UserChangeStatusDTO userChangeStatusDTO);

    /**
     * 删除用户信息(逻辑删除,并清理角色关联与缓存)
     *
     * @param ids 用户ID列表
     */
    void delete(List<Long> ids);

    /**
     * 重置用户密码为系统默认密码
     *
     * @param id 用户ID
     */
    void resetPassword(Long id);

    /**
     * 配置用户角色
     *
     * @param assignRolesDTO 分配角色信息
     */
    void assignRoles(AssignRolesDTO assignRolesDTO);

    /**
     * 查询用户已分配的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByUserId(Long userId);

    /**
     * 失效指定用户的用户信息缓存（userInfo 缓存）
     *
     * @param userIds 用户ID列表
     */
    void evictUserCache(List<Long> userIds);

}
