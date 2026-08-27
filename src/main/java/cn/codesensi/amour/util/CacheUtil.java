package cn.codesensi.amour.util;

import cn.codesensi.amour.context.AppEnvContext;

/**
 * 缓存工具类。
 * <p>
 * 负责为缓存名（cache name）拼接「项目名_运行环境」前缀，用于区分不同项目、不同运行环境下
 * 共享同一缓存服务时的缓存实例，避免数据互相污染。
 * <p>
 * 项目名与运行环境取自 {@link AppEnvContext#getInstance()}（由 Spring 装配的上下文，项目名读
 * yml 的 {@code spring.application.name}，运行环境读当前激活的 Profile）。例如项目名
 * {@code amour}、环境 {@code dev} 时，{@code withAppEnv("captcha")} 返回 {@code amour_dev_captcha}。
 * <p>
 * 本类为纯静态工具类（不标 {@code @Component}），依赖 {@link AppEnvContext} 在应用启动阶段完成
 * 装配；请在 {@link AppEnvContext} 就绪后调用 {@link #withAppEnv(String)}。
 */
public class CacheUtil {

    /**
     * 项目名、运行环境与缓存名之间的分隔符。
     */
    private static final String SEPARATOR = "_";

    /**
     * 为给定缓存名拼接「项目名_运行环境」前缀。
     * <p>
     * 例如 {@code withAppEnv("captcha")} 在项目名 {@code amour}、环境 {@code dev} 时返回
     * {@code amour_dev_captcha}。
     *
     * @param cacheName 基础缓存名，不可为 null
     * @return 拼接后的缓存名
     */
    public static String withAppEnv(String cacheName) {
        AppEnvContext ctx = AppEnvContext.getInstance();
        return ctx.getAppName() + SEPARATOR + ctx.getFirstActiveProfile() + SEPARATOR + cacheName;
    }
}
