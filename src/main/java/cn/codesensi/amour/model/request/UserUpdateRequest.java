package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.constraint.InEnum;
import cn.codesensi.amour.common.consts.RegexConst;
import cn.codesensi.amour.common.enums.GenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = RegexConst.ID_CARD, message = RegexConst.ID_CARD_MESSAGE)
    private String idCard;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 64, message = "用户邮箱长度不能超过64")
    private String email;

    /**
     * 用户手机号码
     */
    @Pattern(regexp = RegexConst.PHONE, message = RegexConst.PHONE_MESSAGE)
    private String phone;

    /**
     * 用户QQ号码
     */
    @Pattern(regexp = RegexConst.QQ, message = RegexConst.QQ_MESSAGE)
    private String qq;

    /**
     * 用户性别:U-未知，M-男，F-女
     */
    @InEnum(enumClass = GenderEnum.class, message = "用户性别不在指定范围内")
    private String gender;

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