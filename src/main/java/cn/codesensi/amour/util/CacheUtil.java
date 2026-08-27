package cn.codesensi.amour.util;

import cn.codesensi.amour.context.AppEnvContext;
import org.springframework.stereotype.Component;

/**
 * 缓存工具类。
 * <p>
 * 负责为缓存名（cache name）拼接「项目名_运行环境」前缀，用于区分不同项目、不同运行环境下
 * 共享同一缓存服务时的缓存实例，避免数据互相污染。
 * <p>
 * 环境信息来源于 {@link AppEnvContext}（项目名取 {@code spring.application.name}，
 * 运行环境取当前激活的 profile）。例如项目名 {@code amour}、环境 {@code dev} 时，
 * {@code withAppEnv("captcha")} 返回 {@code amour_dev_captcha}。
 * <p>
 * 本类为 Spring 容器管理的组件，须在 {@link AppEnvContext} 装配完成后再使用
 * （应用启动后即可）；缓存名通过静态方法 {@link #withAppEnv(String)} 统一获取。
 */
public class CacheUtil {

    /**
     * 项目名、运行环境与缓存名之间的分隔符。
     */
    private static final String SEPARATOR = "_";

    /**
     * 「项目名_运行环境」，如 {@code amour_dev}。
     */
    private static String appEnv;

    /**
     * 从 {@link AppEnvContext} 读取项目名与运行环境，初始化静态前缀。
     *
     * @param appEnvContext 应用环境上下文
     */
    public CacheUtil(AppEnvContext appEnvContext) {
        appEnv = appEnvContext.getAppName() + SEPARATOR + appEnvContext.getFirstActiveProfile();
    }

    /**
     * 为给定缓存名拼接「项目名_运行环境」前缀。
     * <p>
     * 例如 {@code withAppEnv("captcha")} 在 dev 环境返回 {@code amour_dev_captcha}。
     *
     * @param cacheName 基础缓存名，不可为 null
     * @return 拼接后的缓存名
     */
    public static String withAppEnv(String cacheName) {
        return appEnv + SEPARATOR + cacheName;
    }
}
