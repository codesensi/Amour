package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 个人中心-更新当前用户资料参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserProfileUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户性别:U-未知，M-男，F-女
     */
    private String gender;

    /**
     * 用户邮箱
     */
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
