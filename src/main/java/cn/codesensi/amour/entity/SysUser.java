package cn.codesensi.amour.entity;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息实体。
 * <p>
 * 对应 {@code sys_user} 表，存储系统用户的账号凭据与基本资料。
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
public class SysUser extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Id
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户身份证号码
     */
    private String idCard;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 用户性别:U-未知,M-男,F-女
     */
    private String gender;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 用户状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 判断当前用户是否为超级管理员。
     * <p>用户ID等于 {@link RbacConst#USER_ADMIN_ID}（初始化数据中的 1 号账号）即为超级管理员。
     *
     * @return 是超级管理员返回 {@code true}；用户ID为 {@code null}（未持久化的新对象）返回 {@code false}
     */
    public boolean isAdmin() {
        return RbacConst.USER_ADMIN_ID.equals(this.id);
    }

}
