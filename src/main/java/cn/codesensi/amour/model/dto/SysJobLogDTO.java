package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务执行日志 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobLogDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 任务ID
     */
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
     * 链路追踪ID（手动触发沿用发起请求的 traceId，cron 触发执行时新建）
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
