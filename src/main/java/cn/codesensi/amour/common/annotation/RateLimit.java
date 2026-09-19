package cn.codesensi.amour.common.annotation;

import cn.codesensi.amour.common.enums.RateLimitKey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解 —— 标注在 Controller 方法上，按「接口键 + 来源 IP」固定窗口计数限流。
 * <p>
 * 计数存储于 rate-limit 缓存（{@link cn.codesensi.amour.common.consts.CacheConst#RATE_LIMIT}）；
 * 阈值优先从 sys_config（rate-limit.{key}.{limit|window}，INTEGER）读取实现热更新，
 * 未配置时回退本注解的兜底值。超限抛出业务异常，返回 HTTP 429。
 *
 * @author codesensi
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {

    /**
     * 限流键（与 ConfigKeyEnum 的 rate-limit.* 配置键对齐）
     */
    RateLimitKey key();

    /**
     * 兜底最大请求数 —— sys_config 未配置 rate-limit.{key}.limit 时生效
     */
    int fallbackLimit();

    /**
     * 兜底时间窗口（秒）—— sys_config 未配置 rate-limit.{key}.window 时生效
     */
    int fallbackWindowSeconds();

}
