package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 个人中心-修改当前用户名请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserRenameRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 新用户名（登录账号）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "用户名长度不能超过" + AppConst.MAX_LENGTH_64)
    private String username;

}
