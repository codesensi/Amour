package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 门户访问统计累计 DTO —— Service 层出参。
 * <p>
 * 数值为全部日粒度记录的累计值，Controller 经转换器映射为门户响应。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class VisitTotalDTO implements Serializable {

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
