package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增用户请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserInsertDTO implements Serializable {

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
     * 用户QQ号码
     */
    private String qq;

    /**
     * 用户性别:U-未知，M-男，F-女
     */
    private String gender;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 用户状态:0-启用，1-禁用(缺省视为启用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}