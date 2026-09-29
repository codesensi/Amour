package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务 DTO（Service 层进出参，任务分页与调度注册共用）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组（与 JobGroupEnum 对齐：default-默认, infra-基础设施）
     */
    private String jobGroup;

    /**
     * 调用目标（容器内实现 SysTask 接口的 bean 名称）
     */
    private String invokeTarget;

    /**
     * cron 表达式
     */
    private String cronExpression;

    /**
     * 是否允许并发执行: 0-禁止, 1-允许
     */
    private Integer concurrent;

    /**
     * 任务状态: 0-正常, 1-暂停
     */
    private Integer status;

    /**
     * 是否内置: 0-否, 1-是
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最近一次执行状态: 0-失败, 1-成功（从未执行时为 null）
     */
    private Integer lastStatus;

    /**
     * 最近一次执行耗时（毫秒）
     */
    private Long lastDuration;

    /**
     * 最近一次执行开始时间
     */
    private LocalDateTime lastStartTime;

}
