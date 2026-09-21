package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 门户访问统计累计响应结果。
 * <p>
 * 字段名对齐前端契约（VisitTotal），数值为全部日粒度记录的累计值。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class VisitTotalResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 累计浏览量（PV）
     */
    private Long pv;

    /**
     * 累计独立访客数（UV）
     */
    private Long uv;

}
