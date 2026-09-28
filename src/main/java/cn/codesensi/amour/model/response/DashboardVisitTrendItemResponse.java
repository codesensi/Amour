package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 访问趋势条目响应结果。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DashboardVisitTrendItemResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统计日期（yyyy-MM-dd）
     */
    private String statDate;

    /**
     * 当日访问量（PV）
     */
    private Long pv;

    /**
     * 当日独立访客数（UV）
     */
    private Long uv;

}
