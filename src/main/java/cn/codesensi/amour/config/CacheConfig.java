package cn.codesensi.amour.config;

import cn.codesensi.amour.util.CacheUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置。
 * <p>
 * 基于 yml（{@code amour.cache.*}，见 {@link CacheProperties}）逐个注册带各自过期时间的
 * Caffeine 缓存，绕过 Spring Boot 全局统一 spec 的限制。缓存名统一经
 * {@link CacheUtil#withAppEnv(String)} 拼接「项目名_运行环境」前缀，实现多环境隔离。
 * <p>
 * 各缓存的过期时间单位统一为秒，支持「写入后过期」与「访问后过期」两个维度（取先到者，0 表示不限）。
 */
@Configuration
public class CacheConfig {

    /**
     * 构建缓存管理器，依据 yml 配置逐个注册缓存。
     *
     * @param props        缓存配置属性
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(CacheProperties props) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(props.getMaxSize())
                .recordStats());

        // 逐个注册带各自过期时间的缓存，缓存名统一拼接「项目名_运行环境」前缀
        for (CacheProperties.CacheItem item : props.getCaches()) {
            String cacheName = CacheUtil.withAppEnv(item.getName());
            Cache<Object, Object> nativeCache = build(item, props.getMaxSize());
            // registerCustomCache：注册自定义过期策略的原生 Caffeine 缓存（绕过全局 spec）
            manager.registerCustomCache(cacheName, nativeCache);
        }
        return manager;
    }

    /**
     * 依据过期配置构建单个缓存的原生 Caffeine 缓存。
     *
     * @param item    缓存配置项
     * @param maxSize 最大容量（条数）
     * @return 构建完成的原生缓存
     */
    private Cache<Object, Object> build(CacheProperties.CacheItem item, long maxSize) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(maxSize);
        if (item.getExpireAfterWrite() > 0) {
            builder.expireAfterWrite(item.getExpireAfterWrite(), TimeUnit.SECONDS);
        }
        if (item.getExpireAfterAccess() > 0) {
            builder.expireAfterAccess(item.getExpireAfterAccess(), TimeUnit.SECONDS);
        }
        return builder.build();
    }
}
