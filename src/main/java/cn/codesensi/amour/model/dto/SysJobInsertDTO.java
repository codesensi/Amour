package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务新增 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组（与 JobGroupEnum 对齐：default-默认, infra-基础设施）
     */
    private String jobGroup;

    /**
     * 调用目标（容器内实现 SysTask 接口的 bean 名称；Service 层校验白名单）
     */
    private String invokeTarget;

    /**
     * cron 表达式（Service 层做合法性与可解析性校验）
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
