package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 个人中心-修改当前用户名请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserRenameRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 新用户名(登录账号)
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 128, message = "用户名长度不能超过128")
    private String username;

}
