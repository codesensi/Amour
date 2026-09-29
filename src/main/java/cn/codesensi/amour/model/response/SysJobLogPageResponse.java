package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务执行日志分页响应对象。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobLogPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 任务ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long jobId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 触发方式（与 TriggerTypeEnum 对齐：cron-cron调度, manual-手动执行）
     */
    private String triggerType;

    /**
     * 链路追踪ID（与响应头 X-Trace-Id 同源，凭此检索服务端日志）
     */
    private String traceId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 耗时（毫秒）
     */
    private Long duration;

    /**
     * 执行状态: 0-失败, 1-成功
     */
    private Integer status;

    /**
     * 异常信息
     */
    private String errorMsg;

}
