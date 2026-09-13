package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 限流键枚举 —— 与 {@link ConfigKeyEnum} 的 rate-limit.* 配置键及
 * sys_config 初始化数据一一对应，新增限流接口时先在此注册。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum RateLimitKey implements BaseEnum<String> {

    /**
     * 登录接口
     */
    LOGIN("login", "登录接口"),

    /**
     * 验证码接口
     */
    CAPTCHA("captcha", "验证码接口"),

    /**
     * QQ信息查询接口
     */
    QQ_INFO("qq-info", "QQ信息查询接口");

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;
}
