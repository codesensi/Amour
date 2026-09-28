package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 访问趋势条目 DTO。
 * <p>
 * 承载按日粒度的门户访问量（PV）与独立访客数（UV），由 {@code portal_visit} 表聚合而来。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DashboardVisitTrendItemDTO implements Serializable {

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
