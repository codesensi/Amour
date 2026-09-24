package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改点点滴滴文章状态请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsChangeStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long id;

    /**
     * 状态: 0-显示， 1-隐藏
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

}
