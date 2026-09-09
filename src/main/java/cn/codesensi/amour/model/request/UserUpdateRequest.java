package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改用户请求参数
 * <p>
 * 用户名称、状态与密码不在可修改范围，分别由新增、状态管理与重置密码入口维护。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 用户昵称
     */
    @Size(max = 50, message = "用户昵称长度不能超过50")
    private String nickname;

    /**
     * 用户身份证号码
     */
    // @IdCard(message = "身份证号码格式不正确")
    private String idCard;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户手机号码
     */
    // @Phone(message = "手机号格式不正确")
    private String phone;

    /**
     * 用户QQ号码
     */
    private String qq;

    /**
     * 用户性别:U-未知，M-男，F-女
     */
    // @InEnum(enumClass = GenderEnum.class, message = "用户性别不在指定范围内")
    private String gender;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 备注
     */
    private String remark;

}