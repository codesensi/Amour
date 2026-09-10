package cn.codesensi.amour.config;

import cn.codesensi.amour.common.context.AppEnvContext;
import cn.codesensi.amour.common.properties.AppCacheProperties;
import cn.codesensi.amour.common.util.CacheUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置。
 * <p>
 * 基于 yml（{@code app.cache.*}，见 {@link AppCacheProperties}）逐个注册带各自过期时间的
 * Caffeine 缓存，绕过 Spring Boot 全局统一 spec 的限制。缓存名统一经
 * {@link CacheUtil#withAppEnv(String)} 拼接「项目名_运行环境」前缀，实现多环境隔离。
 * <p>
 * 各缓存的过期时间单位统一为秒，支持「写入后过期」与「访问后过期」两个维度（取先到者，0 表示不限）。
 * 过期时间视为基准值，注册时按 {@code app.cache.expire-jitter-percent} 幅度随机抖动（仅基准值
 * 达到 {@code app.cache.expire-jitter-min-seconds} 阈值的长 TTL 缓存参与），
 * 避免多个缓存（如同批预热的条目）在同一时点集中过期导致回源洪峰。
 *
 * @since 1.0
 */
@Configuration
public class CacheConfig {

    /**
     * 构建缓存管理器，依据 yml 配置逐个注册缓存。
     * <p>
     * 通过 {@code @Bean} 方法参数注入 {@link AppEnvContext}：Spring 在调用本方法前会先装配该
     * 参数对应的 Bean，从而保证 {@link AppEnvContext#getInstance()} 已就绪，调用
     * {@link CacheUtil#withAppEnv(String)} 拼接前缀时不会拿到 null。
     *
     * @param appEnvContext 应用环境上下文（仅用于触发其提前装配，保证静态实例可用）
     * @param props         缓存配置属性
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(AppEnvContext appEnvContext, AppCacheProperties props) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(props.getMaxSize())
                .recordStats());

        // 逐个注册带各自过期时间的缓存，缓存名统一拼接「项目名_运行环境」前缀
        for (AppCacheProperties.CacheItem item : props.getCaches()) {
            String cacheName = CacheUtil.withAppEnv(item.getName());
            Cache<Object, Object> nativeCache = build(item, props);
            // registerCustomCache：注册自定义过期策略的原生 Caffeine 缓存（绕过全局 spec）
            manager.registerCustomCache(cacheName, nativeCache);
        }
        return manager;
    }

    /**
     * 依据过期配置构建单个缓存的原生 Caffeine 缓存。
     * <p>
     * 开启 {@code recordStats()} 记录命中/未命中/驱逐等统计，供缓存监控读取；
     * 过期时间经 {@link #applyJitter(long, AppCacheProperties)} 随机抖动后生效。
     *
     * @param item    缓存配置项
     * @param props   缓存配置属性（提供最大容量与抖动配置）
     * @return 构建完成的原生缓存
     */
    private Cache<Object, Object> build(AppCacheProperties.CacheItem item, AppCacheProperties props) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(props.getMaxSize()).recordStats();
        if (item.getExpireAfterWrite() > 0) {
            builder.expireAfterWrite(applyJitter(item.getExpireAfterWrite(), props), TimeUnit.SECONDS);
        }
        if (item.getExpireAfterAccess() > 0) {
            builder.expireAfterAccess(applyJitter(item.getExpireAfterAccess(), props), TimeUnit.SECONDS);
        }
        return builder.build();
    }

    /**
     * 对过期时间施加随机抖动，避免同一批写入的缓存在同一时点集中过期。
     * <p>
     * 在基准值上下按配置幅度（百分比）均匀随机偏移，如 30 天基准 ±10% 后落在 27~33 天之间，
     * 各缓存实例每次启动独立取值，实现错峰。仅基准值达到 {@code expire-jitter-min-seconds}
     * 阈值的长 TTL 缓存参与抖动（短 TTL 缓存如验证码、QQ 信息保持精确语义）；
     * 幅度或偏移量为 0 时原值返回。
     *
     * @param seconds 基准过期时间（秒），调用方保证为正
     * @param props   缓存配置属性（提供抖动幅度与生效阈值）
     * @return 抖动后的过期时间（秒），恒为正且不低于基准的 (100 - jitterPercent)%
     */
    private long applyJitter(long seconds, AppCacheProperties props) {
        long spread = seconds * props.getExpireJitterPercent() / 100;
        if (spread <= 0 || seconds < props.getExpireJitterMinSeconds()) {
            return seconds;
        }
        // 在 [seconds - spread, seconds + spread] 闭区间内均匀随机
        return seconds - spread + (long) (ThreadLocalRandom.current().nextDouble() * (2 * spread + 1));
    }
}
