package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.AssignRolesDTO;
import cn.codesensi.amour.model.dto.UserInfoDTO;
import cn.codesensi.amour.model.dto.UserPageDTO;
import cn.codesensi.amour.model.dto.UserSaveDTO;
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
     * 保存用户信息
     *
     * @param userSaveDTO 用户信息
     */
    void saveUser(UserSaveDTO userSaveDTO);

    /**
     * 配置用户角色
     *
     * @param assignRolesDTO 分配角色信息
     */
    void assignRoles(AssignRolesDTO assignRolesDTO);

    /**
     * 失效指定用户的用户信息缓存（userInfo 缓存）
     *
     * @param userIds 用户ID列表
     */
    void evictUserCache(List<Long> userIds);

}
