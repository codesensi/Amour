package cn.codesensi.amour.model.entity;

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
     * 用户QQ号码
     */
    private String qq;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 用户状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 内置标识:0-非内置,1-内置
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

}
