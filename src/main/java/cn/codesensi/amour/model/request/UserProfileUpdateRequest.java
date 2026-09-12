package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.RegexConst;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = RegexConst.GENDER, message = RegexConst.GENDER_MESSAGE)
    private String gender;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 64, message = "用户邮箱长度不能超过64")
    private String email;

    /**
     * 用户QQ号码
     */
    @Pattern(regexp = RegexConst.QQ, message = RegexConst.QQ_MESSAGE)
    private String qq;

    /**
     * 用户头像地址
     */
    @Size(max = 512, message = "用户头像地址长度不能超过512")
    private String avatar;

    /**
     * 备注
     */
    @Size(max = 512, message = "备注长度不能超过512")
    private String remark;

}
