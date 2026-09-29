package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务执行日志分页查询参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysJobLogPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（可空，空时查全部任务的日志）
     */
    private Long jobId;

    /**
     * 执行状态: 0-失败， 1-成功（可空）
     */
    private Integer status;

}
