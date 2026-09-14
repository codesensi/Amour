package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 缓存名枚举 —— 项目内全部缓存名的唯一来源（代码侧消费与 yml 注册完备性校验共用）。
 * <p>
 * 新增缓存时仅需在本枚举追加常量并同步补充 yml（{@code app.cache.caches}），
 * 漏配将由 CacheConfig 启动期校验拦截，避免配置错误在运行期静默失效。
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum CacheNameEnum implements BaseEnum<String> {

    /** 验证码缓存：Key 为 captchaKey，存储验证码答案 */
    CAPTCHA("captcha", "验证码"),

    /** 系统配置缓存：Key 为配置键，存储 sys_config 配置实体 */
    CONFIG("config", "系统配置"),

    /** 角色编码缓存：Key 为用户ID，供 Sa-Token 鉴权读取角色编码列表 */
    ROLE("role", "角色编码"),

    /** 权限编码缓存：Key 为用户ID，供 Sa-Token 鉴权读取权限编码列表 */
    PERM("perm", "权限编码"),

    /** 路由菜单缓存：Key 为用户ID，存储用户可访问的路由菜单列表 */
    MENU("menu", "路由菜单"),

    /** 用户信息缓存：Key 为用户ID，存储用户资料快照 */
    USER("user", "用户信息"),

    /** QQ 信息缓存：Key 为 QQ 号，存储上游服务解析出的头像地址与昵称 */
    QQ_INFO("qq-info", "QQ信息"),

    /** 数据字典缓存：Key 为字典编码（dict_code），存储该编码下启用中的字典项列表 */
    DICT("dict", "数据字典"),

    /** 接口限流计数缓存：Key 为「接口键:IP」，存储固定窗口计数器 */
    RATE_LIMIT("rate-limit", "接口限流计数"),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;
}
