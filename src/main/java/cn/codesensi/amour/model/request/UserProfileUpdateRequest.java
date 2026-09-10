package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 个人中心-更新当前用户资料请求参数
 * <p>
 * 白名单字段：用户名、密码、状态等不在可修改范围，分别由改名、改密与状态管理入口维护。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserProfileUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 50, message = "用户昵称长度不能超过50")
    private String nickname;

    /**
     * 用户性别:U-未知，M-男，F-女
     */
    private String gender;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户QQ号码
     */
    private String qq;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 备注
     */
    private String remark;

}
