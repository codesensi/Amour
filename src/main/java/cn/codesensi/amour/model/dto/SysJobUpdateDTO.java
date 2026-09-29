package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务修改 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组（与 JobGroupEnum 对齐；仅非内置任务可重新归类，内置任务忽略此字段）
     */
    private String jobGroup;

    /**
     * cron 表达式
     */
    private String cronExpression;

    /**
     * 是否允许并发执行: 0-禁止， 1-允许
     */
    private Integer concurrent;

    /**
     * 备注
     */
    private String remark;

}
