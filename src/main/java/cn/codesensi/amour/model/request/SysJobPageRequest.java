package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务分页查询请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysJobPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称（模糊匹配；可空）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务名称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobName;

    /**
     * 任务分组（精确匹配；可空）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "任务分组长度不能超过" + AppConst.MAX_LENGTH_64)
    private String jobGroup;

    /**
     * 任务状态: 0-正常, 1-暂停（可空）
     */
    @Min(value = 0, message = "任务状态取值不合法")
    @Max(value = 1, message = "任务状态取值不合法")
    private Integer status;

}
