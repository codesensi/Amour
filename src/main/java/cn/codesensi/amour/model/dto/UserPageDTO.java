package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户分页查询参数
 *
 * @author codesensi
 * @since 2026-09-04
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称(模糊匹配)
     */
    private String username;

    /**
     * 用户昵称(模糊匹配)
     */
    private String nickname;

    /**
     * 用户身份证号码(模糊匹配)
     */
    private String idCard;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 用户QQ号码(模糊匹配)
     */
    private String qq;

    /**
     * 用户邮箱(模糊匹配)
     */
    private String email;

    /**
     * 用户性别:U-未知,M-男,F-女
     */
    private String gender;

    /**
     * 用户状态:0-启用,1-禁用
     */
    private Integer status;

}
