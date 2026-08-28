package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 缓存内容响应 —— 面向缓存查询结果的数据传输对象。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class CacheResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 缓存名（含「项目名_运行环境」前缀，如 amour_dev_config）
     */
    private String cacheName;

    /**
     * 写入后过期时间（秒）；null 表示不限制
     */
    private Long expireAfterWrite;

    /**
     * 访问后过期时间（秒）；null 表示不限制
     */
    private Long expireAfterAccess;

    /**
     * 最大容量（条数）
     */
    private Long maximumSize;

    /**
     * 缓存条目列表（含每条目的键、值与剩余过期时间）
     */
    private List<CacheEntryResponse> entries;
}
