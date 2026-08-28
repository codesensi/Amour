package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 缓存条目响应 —— 单条缓存内容的键值与剩余过期时间。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class CacheEntryResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 缓存键
     */
    private String key;

    /**
     * 缓存值（值为 null 表示该键缓存的是"数据不存在"的空值哨兵占位）
     */
    private Object value;

    /**
     * 剩余过期时间（秒）；null 表示缓存驻留不过期
     */
    private Long remainExpire;
}
