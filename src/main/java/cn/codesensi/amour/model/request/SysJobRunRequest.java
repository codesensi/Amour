package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务手动执行请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobRunRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（雪花ID字符串化传输）
     */
    @NotBlank(message = "任务ID不能为空")
    private String id;

}
