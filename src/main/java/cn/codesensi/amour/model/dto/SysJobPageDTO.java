package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务分页查询参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysJobPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称（模糊匹配；可空）
     */
    private String jobName;

    /**
     * 任务分组（精确匹配；可空）
     */
    private String jobGroup;

    /**
     * 任务状态: 0-正常， 1-暂停（可空）
     */
    private Integer status;

}
