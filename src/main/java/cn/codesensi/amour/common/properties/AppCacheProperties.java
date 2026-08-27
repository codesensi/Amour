package cn.codesensi.amour.common.properties;

import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.config.CacheConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存配置属性，可从 yml（前缀 {@code app.cache.*}）绑定，由 {@link CacheConfig} 消费。
 * <p>
 * 过期时间单位统一为「秒」，支持写入后与访问后两个维度，取先到者生效：
 * <ul>
 *   <li>{@code expireAfterWrite} 为写入后过期时间；</li>
 *   <li>{@code expireAfterAccess} 为访问后过期时间；</li>
 *   <li>{@code 0} 表示该维度不限制。</li>
 * </ul>
 */
@Data
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {

    /**
     * 全局兜底最大容量（条数）。
     */
    private long maxSize = 1000L;

    /**
     * 各缓存个性化配置列表。
     */
    private List<CacheItem> caches = new ArrayList<>();

    /**
     * 单个缓存的配置项。
     */
    @Data
    public static class CacheItem {

        /**
         * 基础缓存名（不含「项目名_运行环境」前缀，实际注册时由 {@link CacheUtil#withAppEnv(String)} 拼接）。
         */
        private String name;

        /**
         * 写入后过期时间（秒）；0 表示不限。
         */
        private long expireAfterWrite;

        /**
         * 访问后过期时间（秒）；0 表示不限。
         */
        private long expireAfterAccess;

    }
}
