package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 限流配置键的字段段 —— 与 {@link ConfigKeyEnum} 中 rate-limit.* 配置键的最后一段对应，
 * 消除切面组装配置键时的散落字符串。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum RateLimitField implements BaseEnum<String> {

    /**
     * 窗口内最大请求数
     */
    LIMIT("limit", "窗口内最大请求数"),

    /**
     * 时间窗口（秒）
     */
    WINDOW("window", "时间窗口(秒)");

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;
}
