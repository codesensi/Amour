package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 系统配置键枚举 —— 与 sys_config 表初始化数据（sql/init_dml.sql）中的配置键一一对应。
 * <p>
 * 统一以枚举常量引用配置键，避免调用侧散落魔法字符串；
 * 新增配置时请同步补充初始化 DML 脚本与本枚举。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum ConfigKeyEnum implements BaseEnum<String> {

    // ---------- 基础配置（base 分组：1000 段） ----------
    NAME("name", "项目/站点名称"),
    LOGO("logo", "项目/站点logo图片"),
    FAVICON("favicon", "项目/站点favicon图标"),
    ICP("icp", "ICP备案文案"),
    COPYRIGHT_YEAR("copyright-year", "版权年份"),
    TRUST_PROXY_HEADERS("trust-proxy-headers", "是否信任代理头(X-Real-IP等)"),

    // ---------- 门户站点配置（site 分组：2000 段） ----------
    SITE_LOVE_START_DATE("site.love-start-date", "门户恋爱计时起点"),

    // ---------- 验证码配置（captcha 分组：3000 段） ----------
    CAPTCHA_ENABLED("captcha.enabled", "验证码开关"),
    CAPTCHA_IMAGE_TYPE("captcha.image-type", "图形验证码类型"),

    // ---------- 文件配置（file 分组：4000 段） ----------
    FILE_STORAGE("file.storage", "文件存储方式"),

    // ---------- 接口限流（rate-limit 分组：5000 段） ----------
    RATE_LIMIT_LOGIN_LIMIT(key(RateLimitKey.LOGIN, RateLimitField.LIMIT), "登录接口-窗口内最大请求数"),
    RATE_LIMIT_LOGIN_WINDOW(key(RateLimitKey.LOGIN, RateLimitField.WINDOW), "登录接口-时间窗口(秒)"),
    RATE_LIMIT_CAPTCHA_LIMIT(key(RateLimitKey.CAPTCHA, RateLimitField.LIMIT), "验证码接口-窗口内最大请求数"),
    RATE_LIMIT_CAPTCHA_WINDOW(key(RateLimitKey.CAPTCHA, RateLimitField.WINDOW), "验证码接口-时间窗口(秒)"),
    RATE_LIMIT_QQ_INFO_LIMIT(key(RateLimitKey.QQ_INFO, RateLimitField.LIMIT), "QQ信息接口-窗口内最大请求数"),
    RATE_LIMIT_QQ_INFO_WINDOW(key(RateLimitKey.QQ_INFO, RateLimitField.WINDOW), "QQ信息接口-时间窗口(秒)"),
    RATE_LIMIT_AMAP_PROXY_LIMIT(key(RateLimitKey.AMAP_PROXY, RateLimitField.LIMIT), "高德服务代理接口-窗口内最大请求数"),
    RATE_LIMIT_AMAP_PROXY_WINDOW(key(RateLimitKey.AMAP_PROXY, RateLimitField.WINDOW), "高德服务代理接口-时间窗口(秒)"),
    RATE_LIMIT_SAYING_LIMIT(key(RateLimitKey.SAYING, RateLimitField.LIMIT), "一言接口-窗口内最大请求数"),
    RATE_LIMIT_SAYING_WINDOW(key(RateLimitKey.SAYING, RateLimitField.WINDOW), "一言接口-时间窗口(秒)"),
    RATE_LIMIT_MESSAGE_LIMIT(key(RateLimitKey.MESSAGE, RateLimitField.LIMIT), "留言提交接口-窗口内最大请求数"),
    RATE_LIMIT_MESSAGE_WINDOW(key(RateLimitKey.MESSAGE, RateLimitField.WINDOW), "留言提交接口-时间窗口(秒)"),

    // ---------- 安全配置（security 分组：6000 段） ----------
    SECURITY_UAPI_KEY("security.uapi-key", "UApiPro接口密钥"),
    SECURITY_AMAP_KEY("security.amap-key", "高德地图Web端JS API Key"),
    SECURITY_AMAP_CODE("security.amap-code", "高德地图安全密钥");

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

    /**
     * 按限流键与字段组装 rate-limit 配置键枚举。
     * <p>
     * 键的组装规则以 {@link #key} 为唯一定义点；限流键对应的配置键尚未在枚举中注册时
     * 返回 {@code null}，调用侧应回退兜底阈值并告警，避免动态阈值静默失效。
     *
     * @param limitKey 限流键
     * @param field    字段
     * @return 对应的配置键枚举，无匹配时为 {@code null}
     */
    public static ConfigKeyEnum rateLimitKeyOf(RateLimitKey limitKey, RateLimitField field) {
        return BaseEnum.fromCode(ConfigKeyEnum.class, key(limitKey, field));
    }

    /**
     * rate-limit 配置键前缀 —— 组装 rate-limit.&lt;key&gt;.&lt;field&gt; 时共用（见 {@link #key}）
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit";

    /**
     * 组装 rate-limit 配置键（{@code rate-limit.<key>.<field>}）——
     * 枚举常量与 {@link #rateLimitKeyOf} 共用本方法，保证键前缀单一来源。
     *
     * @param limitKey 限流键
     * @param field    字段
     * @return 完整配置键
     */
    private static String key(RateLimitKey limitKey, RateLimitField field) {
        return RATE_LIMIT_KEY_PREFIX + AppConst.DOT + limitKey.getCode() + AppConst.DOT + field.getCode();
    }
}
