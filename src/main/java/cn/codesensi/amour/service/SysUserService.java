package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户信息 Service。
 * <p>
 * 继承 MyBatis-Flex 的 {@link IService}，开箱即得 {@code sys_user} 表的增删改查能力；
 * 业务方法覆盖分页查询、资料维护、状态与角色管理及密码重置。
 *
 * @since 1.0
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
    UserInfoDTO getCurrentUser();

    /**
     * 新增用户信息
     *
     * @param userInsertDTO 用户信息
     */
    void insert(UserInsertDTO userInsertDTO);

    /**
     * 修改用户信息(仅资料字段，用户名、状态与密码不在可修改范围)
     *
     * @param userUpdateDTO 用户信息
     */
    void update(UserUpdateDTO userUpdateDTO);

    /**
     * 更新当前登录用户资料(仅白名单资料字段，变更后失效 userInfo 缓存)
     *
     * @param userProfileUpdateDTO 资料信息
     */
    void updateProfile(UserProfileUpdateDTO userProfileUpdateDTO);

    /**
     * 修改当前登录用户名(用户名为登录凭证，修改成功后踢出会话要求重新登录)
     *
     * @param username 新用户名
     */
    void rename(String username);

    /**
     * 修改当前登录用户密码(校验原密码，修改成功后踢出会话要求重新登录)
     *
     * @param userPasswordUpdateDTO 密码信息
     */
    void updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO);

    /**
     * 修改用户状态(系统内置用户不允许停用)
     *
     * @param userChangeStatusDTO 状态信息
     */
    void changeStatus(UserChangeStatusDTO userChangeStatusDTO);

    /**
     * 删除用户信息(逻辑删除，并清理角色关联与缓存)
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

}