package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改足迹显隐请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FootprintChangeHiddenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 足迹ID
     */
    @NotNull(message = "足迹ID不能为空")
    private Long id;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    @NotNull(message = "显隐标识不能为空")
    private Integer hidden;

}
