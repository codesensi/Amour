package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求参数
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@Data
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户密码（账号登录时必填）
     */
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
