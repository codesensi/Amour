package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言地区分布条目 DTO。
 * <p>
 * 承载按 IP 归属地聚合的留言计数，用于首页看板的地区分布展示。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DashboardMessageRegionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * IP 归属地（无法识别的留言归并为「未知」）
     */
    private String region;

    /**
     * 留言条数
     */
    private Long count;

}
