package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = AppConst.USERNAME_MAX_LENGTH, message = "账号长度不能超过" + AppConst.USERNAME_MAX_LENGTH)
    private String username;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = AppConst.PASSWORD_MAX_LENGTH, message = "密码长度不能超过" + AppConst.PASSWORD_MAX_LENGTH)
    private String password;

    /**
     * 验证码唯一标识
     */
    private String captchaKey;

    /**
     * 验证码内容
     */
    private String captchaValue;

}
