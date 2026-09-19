package cn.codesensi.amour.common.aspect;

import cn.codesensi.amour.common.annotation.RateLimit;
import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.ResultCode;
import cn.codesensi.amour.common.enums.CacheNameEnum;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.enums.RateLimitField;
import cn.codesensi.amour.common.enums.RateLimitKey;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.common.util.IpUtil;
import cn.codesensi.amour.common.util.ServletUtil;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * 接口限流切面 —— 拦截标注了 {@link RateLimit} 的接口，按「接口键 + 来源 IP」固定窗口计数限流。
 * <p>
 * 计数存储于统一的 rate-limit 缓存（{@link CacheNameEnum#RATE_LIMIT}，经 CacheManager 装配，
 * 与项目其他缓存共用多环境前缀与容量/过期策略）；限流窗口由计数器自带时间戳管理，
 * 缓存条目的过期仅作内存回收，因此 sys_config 动态调整窗口不会导致计数提前清零。
 * <p>
 * 阈值解析顺序：sys_config（rate-limit.{key}.{limit|window}，INTEGER）优先，
 * 未配置、已停用或值非法时回退注解兜底值；超限抛出业务异常（HTTP 429）。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    /**
     * 限流键到 sys_config 配置键的映射 —— 启动期构建并校验：
     * {@link RateLimitKey} 已注册而 {@link ConfigKeyEnum} 未注册对应配置键时直接启动失败，
     * 避免动态阈值在运行期静默失效。
     */
    private static final Map<RateLimitKey, ConfigKeys> CONFIG_KEYS = buildConfigKeys();

    private final CacheManager cacheManager;

    private final SysConfigService sysConfigService;

    /**
     * 限流校验：计数未超限则放行，超限抛出 {@link BusinessException}（HTTP 429）。
     *
     * @param rateLimit 限流注解
     */
    @Before("@annotation(rateLimit)")
    public void check(RateLimit rateLimit) {
        // 代理头可信开关:sys_config 基础分组（trust-proxy-headers）热更新，仅部署于可信反向代理之后时开启;
        // 直连形态取连接对端地址，避免客户端伪造 X-Real-IP 等代理头绕过限流
        SysConfig trustSwitch = sysConfigService.oneByKey(ConfigKeyEnum.TRUST_PROXY_HEADERS.getCode());
        boolean trustProxyHeaders = ObjUtil.isNotNull(trustSwitch) && Boolean.parseBoolean(trustSwitch.getConfigValue());
        String ip = IpUtil.getIpAddr(ServletUtil.getRequest(), trustProxyHeaders);
        ConfigKeys configKeys = CONFIG_KEYS.get(rateLimit.key());
        int limit = resolve(configKeys.limit(), rateLimit.fallbackLimit(), 0);
        long windowMillis = resolve(configKeys.window(), rateLimit.fallbackWindowSeconds(), 1) * 1000L;

        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheNameEnum.RATE_LIMIT.getCode()));
        if (ObjUtil.isNull(cache)) {
            // 缓存未注册（如 yml 漏配）：fail-open，限流失效但不阻断业务；
            // 注册完备性已由 CacheConfig 启动期校验兜底，正常配置下不会走到这里，error 级别确保可被监控发现
            log.error("rate-limit 缓存未注册，限流已失效，请检查 app.cache.caches 配置");
            return;
        }
        long now = System.currentTimeMillis();
        // get（key， callable） 原子创建计数器；窗口判断与重置在计数器内部同步完成
        WindowCounter counter = cache.get(rateLimit.key().getCode() + AppConst.COLON + ip, () -> new WindowCounter(now));
        if (ObjUtil.isNull(counter)) {
            // 理论不可达：该重载契约是未命中时执行 callable 并返回其结果，callable 恒非 null；
            // 显式防御接口的 @Nullable 标注，与缓存未注册同策略 fail-open
            return;
        }
        if (counter.increment(now, windowMillis) > limit) {
            log.warn("接口限流触发：key={}，ip={}", rateLimit.key().getCode(), ip);
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS.getCode(),
                    ResultCode.TOO_MANY_REQUESTS.getMsg());
        }
    }

    /**
     * 解析限流阈值：sys_config 配置（configKey）优先；未配置、已停用、值非法或小于允许最小值时
     * 回退注解兜底值。
     *
     * @param configKey 配置键枚举（rate-limit.*）
     * @param fallback  兜底值
     * @param minValue  允许的最小配置值（limit 允许 0 表示拒绝全部请求；window 须大于 0）
     * @return 生效阈值
     */
    private int resolve(ConfigKeyEnum configKey, int fallback, int minValue) {
        try {
            SysConfig config = sysConfigService.oneByKey(configKey.getCode());
            if (ObjUtil.isNotNull(config) && StrUtil.isNotBlank(config.getConfigValue())) {
                int value = Integer.parseInt(config.getConfigValue().trim());
                if (value < minValue) {
                    log.warn("限流阈值配置小于允许最小值，回退兜底值：key={}，value={}，minValue={}，fallback={}",
                            configKey.getCode(), value, minValue, fallback);
                    return fallback;
                }
                return value;
            }
        } catch (NumberFormatException e) {
            log.warn("限流阈值配置非数字，回退兜底值：key={}，fallback={}", configKey.getCode(), fallback);
        }
        return fallback;
    }

    /**
     * 启动期构建限流键与配置键的映射；{@link RateLimitKey} 已注册而 {@link ConfigKeyEnum}
     * 漏加配置键时，抛出异常使应用启动失败，确保「枚举注册—配置键—初始化数据」三者同步。
     *
     * @return 限流键到配置键的不可变映射
     */
    private static Map<RateLimitKey, ConfigKeys> buildConfigKeys() {
        Map<RateLimitKey, ConfigKeys> keys = new EnumMap<>(RateLimitKey.class);
        for (RateLimitKey key : RateLimitKey.values()) {
            ConfigKeyEnum limit = ConfigKeyEnum.rateLimitKeyOf(key, RateLimitField.LIMIT);
            ConfigKeyEnum window = ConfigKeyEnum.rateLimitKeyOf(key, RateLimitField.WINDOW);
            if (ObjUtil.isNull(limit) || ObjUtil.isNull(window)) {
                throw new IllegalStateException("限流键缺少对应的 sys_config 配置键枚举：key=" + key.getCode()
                        + "，请在 ConfigKeyEnum 中补充 rate-limit." + key.getCode() + ".{limit|window}");
            }
            keys.put(key, new ConfigKeys(limit, window));
        }
        return Map.copyOf(keys);
    }

    /**
     * 限流键对应的一对 sys_config 配置键（窗口内最大请求数与时间窗口）。
     */
    private record ConfigKeys(ConfigKeyEnum limit, ConfigKeyEnum window) {
    }

    /**
     * 固定窗口计数器 —— 窗口起点由自身记录（不依赖缓存过期），跨窗口时在同步块内重置；
     * 缓存层的条目过期仅作内存回收，因此 sys_config 动态调整窗口不会导致计数提前清零。
     */
    private static class WindowCounter {

        private long startMillis;
        private long count;

        private WindowCounter(long startMillis) {
            this.startMillis = startMillis;
        }

        /**
         * 计数累加；已跨入新窗口时先重置起点与计数（同步块内执行，无竞态）。
         *
         * @param now          当前时间戳（毫秒）
         * @param windowMillis 窗口大小（毫秒）
         * @return 当前窗口内的请求计数（含本次）
         */
        private synchronized long increment(long now, long windowMillis) {
            if (now - startMillis >= windowMillis) {
                startMillis = now;
                count = 0;
            }
            return ++count;
        }
    }
}
