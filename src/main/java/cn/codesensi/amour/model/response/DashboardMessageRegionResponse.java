package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言地区分布条目响应结果。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DashboardMessageRegionResponse implements Serializable {

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
