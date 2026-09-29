package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改定时任务请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（雪花ID字符串化传输）
     */
    @NotBlank(message = "任务ID不能为空")
    private String id;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务名称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobName;

    /**
     * 任务分组（与 JobGroupEnum 对齐；仅非内置任务可重新归类，内置任务忽略此字段）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务分组长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobGroup;

    /**
     * cron 表达式（Service 层做合法性与可解析性校验）
     */
    @NotBlank(message = "cron 表达式不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "cron 表达式长度不能超过" + AppConst.MAX_LENGTH_64)
    private String cronExpression;

    /**
     * 是否允许并发执行: 0-禁止， 1-允许
     */
    private Integer concurrent;

    /**
     * 备注（可空）
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "备注长度不能超过" + AppConst.MAX_LENGTH_512)
    private String remark;

}
