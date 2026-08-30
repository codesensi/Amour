package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存用户请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
public class UserSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String username;

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
     * 用户性别:0-保密,1-男,2-女
     */
    private Integer gender;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 备注
     */
    private String remark;

}
