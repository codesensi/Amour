package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务执行日志实体。
 * <p>
 * 对应 {@code sys_job_log} 表，记录每一次任务触发（cron 或手动）的开始时间、
 * 耗时与结果；继承审计列与逻辑删除标识，日志默认逻辑删除，
 * 任务删除与滚动清理时经 {@code LogicDeleteManager} 物理删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_job_log")
public class SysJobLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 任务ID
     */
    private Long jobId;

    /**
     * 任务名称（冗余，任务删除后日志仍可读）
     */
    private String jobName;

    /**
     * 触发方式（与 TriggerTypeEnum 对齐：cron-cron调度, manual-手动执行）
     */
    private String triggerType;

    /**
     * 链路追踪ID（手动触发沿用发起请求的 traceId，cron 触发执行时新建，与响应头 X-Trace-Id 同源）
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
