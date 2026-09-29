package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务实体。
 * <p>
 * 对应 {@code sys_job} 表，承载可动态调度的任务定义：cron 表达式经
 * {@code SysJobScheduleHolder} 注册到 Spring TaskScheduler，暂停/恢复/删除
 * 均实时热更新调度容器。调用目标仅允许容器内实现了 {@code SysTask} 接口的 bean。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_job")
public class SysJob extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
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
     * 是否内置: 0-否, 1-是（内置任务不可删除）
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

}
