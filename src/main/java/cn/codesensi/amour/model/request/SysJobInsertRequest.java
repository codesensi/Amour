package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增定时任务请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务名称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobName;

    /**
     * 任务分组（与 JobGroupEnum 对齐：default-默认, infra-基础设施；可空，缺省 default-默认）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务分组长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobGroup;

    /**
     * 调用目标（容器内实现 SysTask 接口的 bean 名称；Service 层校验白名单）
     */
    @NotBlank(message = "调用目标不能为空")
    @Size(max = AppConst.MAX_LENGTH_128, message = "调用目标长度不能超过" + AppConst.MAX_LENGTH_128)
    private String invokeTarget;

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
