package cn.codesensi.amour.common.properties;

import cn.codesensi.amour.common.util.CacheUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存配置属性，可从 yml（前缀 {@code app.cache.*}）绑定，由 config 包的 CacheConfig 消费。
 * <p>
 * 过期时间单位统一为「秒」，支持写入后与访问后两个维度，取先到者生效：
 * <ul>
 *   <li>{@code expireAfterWrite} 为写入后过期时间；</li>
 *   <li>{@code expireAfterAccess} 为访问后过期时间；</li>
 *   <li>{@code 0} 表示该维度不限制。</li>
 * </ul>
 * <p>
 * 过期时间作为基准值，实际生效时按 {@code expireJitterPercent} 幅度随机抖动，
 * 避免多个缓存（如同批预热的条目）在同一时点集中过期导致回源洪峰。
 *
 * @since 1.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {

    /**
     * 全局兜底最大容量（条数）。
     */
    private long maxSize = 1000L;

    /**
     * 过期时间随机抖动幅度（百分比，如 {@code 10} 表示在基准值上下 ±10% 内随机）；0 表示关闭抖动。
     */
    private int expireJitterPercent = 10;

    /**
     * 参与随机抖动的最小过期时间（秒）：基准值达到该阈值的缓存才参与抖动，
     * 短 TTL 缓存（如验证码、QQ 信息）保持精确语义；0 表示全部参与。
     */
    private long expireJitterMinSeconds = 0;

    /**
     * 各缓存个性化配置列表。
     */
    @Valid
    private List<CacheItem> caches = new ArrayList<>();

    /**
     * 单个缓存的配置项。
     */
    @Data
    public static class CacheItem {

        /**
         * 基础缓存名（不含「项目名_运行环境」前缀，实际注册时由 {@link CacheUtil#withAppEnv(String)} 拼接）。
         */
        @NotBlank(message = "缓存配置项 name 不能为空")
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
