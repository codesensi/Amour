package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 缓存命中统计响应 —— 单个缓存的命中与加载统计数据。
 * <p>
 * 数据来源于 Caffeine 原生 {@code CacheStats}（需缓存构建时开启 {@code recordStats()}），
 * 为自缓存实例创建（应用启动）起的累计值，重启后归零。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class CacheStatsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 读取命中次数
     */
    private Long hitCount;

    /**
     * 读取未命中次数（含首次加载与回源未命中）
     */
    private Long missCount;

    /**
     * 命中率（0~1）
     */
    private Double hitRate;

    /**
     * 驱逐次数（容量驱逐与过期驱逐合计）
     */
    private Long evictionCount;

    /**
     * 回源加载成功次数（未命中后查库回填）
     */
    private Long loadSuccessCount;

    /**
     * 回源加载失败次数（查库抛异常等）
     */
    private Long loadFailureCount;

    /**
     * 平均回源加载耗时（毫秒）
     */
    private Double averageLoadPenaltyMillis;
}
