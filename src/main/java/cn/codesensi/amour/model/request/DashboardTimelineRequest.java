package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * 首页最近回忆时间线分页请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DashboardTimelineRequest extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;

}
