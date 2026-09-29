package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务启停请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobChangeStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（雪花ID字符串化传输）
     */
    @NotBlank(message = "任务ID不能为空")
    private String id;

    /**
     * 目标状态: 0-正常(恢复)， 1-暂停
     */
    @Min(value = 0, message = "任务状态取值不合法")
    @Max(value = 1, message = "任务状态取值不合法")
    private Integer status;

}
