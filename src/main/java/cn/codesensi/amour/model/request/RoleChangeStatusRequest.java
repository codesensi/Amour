package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改角色状态请求参数
 *
 * @author codesensi
 * @since 2026-09-08
 */
@Data
public class RoleChangeStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /**
     * 角色状态:0-启用,1-禁用
     */
    @NotNull(message = "角色状态不能为空")
    private Integer status;

}
