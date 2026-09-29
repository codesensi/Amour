package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务执行日志分页查询请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysJobLogPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（雪花ID字符串化传输；可空，空时查全部任务的日志）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务ID长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobId;

    /**
     * 执行状态: 0-失败, 1-成功（可空）
     */
    @Min(value = 0, message = "执行状态取值不合法")
    @Max(value = 1, message = "执行状态取值不合法")
    private Integer status;

}
