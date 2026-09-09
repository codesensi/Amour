package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改用户状态请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserChangeStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 用户状态:0-启用，1-禁用
     */
    @NotNull(message = "用户状态不能为空")
    private Integer status;

}