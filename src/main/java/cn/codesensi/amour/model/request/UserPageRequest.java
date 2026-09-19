package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户分页查询请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "用户名称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String username;

    /**
     * 用户昵称（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "用户昵称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String nickname;

    /**
     * 用户身份证号码（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "用户身份证号码长度不能超过" + AppConst.MAX_LENGTH_64)
    private String idCard;

    /**
     * 用户手机号码
     */
    @Size(max = AppConst.MAX_LENGTH_16, message = "用户手机号码长度不能超过" + AppConst.MAX_LENGTH_16)
    private String phone;

    /**
     * 用户QQ号码（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_16, message = "QQ号码长度不能超过" + AppConst.MAX_LENGTH_16)
    private String qq;

    /**
     * 用户邮箱（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "用户邮箱长度不能超过" + AppConst.MAX_LENGTH_64)
    private String email;

    /**
     * 用户性别:U-未知，M-男，F-女
     */
    private String gender;

    /**
     * 用户状态:0-启用，1-禁用
     */
    private Integer status;

}