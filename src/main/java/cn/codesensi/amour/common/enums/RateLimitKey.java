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
    QQ_INFO("qq-info", "QQ信息查询接口"),

    /**
     * 高德服务代理接口
     */
    AMAP_PROXY("amap-proxy", "高德服务代理接口"),

    /**
     * 一言接口（免登录直连上游，需防滥用）
     */
    SAYING("saying", "一言接口"),

    /**
     * 留言提交接口（门户免登录可写，需防刷屏）
     */
    MESSAGE("message", "留言提交接口");

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;
}
