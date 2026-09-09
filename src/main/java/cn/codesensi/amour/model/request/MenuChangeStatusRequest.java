package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改菜单状态请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MenuChangeStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @NotNull(message = "菜单ID不能为空")
    private Long id;

    /**
     * 菜单状态:0-启用，1-禁用
     */
    @NotNull(message = "菜单状态不能为空")
    private Integer status;

}