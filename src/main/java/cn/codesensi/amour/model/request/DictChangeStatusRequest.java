package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改字典状态请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictChangeStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典条目ID
     */
    @NotNull(message = "字典条目ID不能为空")
    private Long id;

    /**
     * 字典状态:0-启用，1-禁用
     */
    @NotNull(message = "字典状态不能为空")
    private Integer status;

}