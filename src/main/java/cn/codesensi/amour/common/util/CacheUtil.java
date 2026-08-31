package cn.codesensi.amour.common.util;

import cn.codesensi.amour.common.context.AppEnvContext;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    /**
     * 在当前事务提交后执行缓存失效动作。
     * <p>
     * 写库事务内直接失效缓存存在时序窗口：事务提交前，其他请求回源查库读不到未提交数据，
     * 会把旧值重新写回缓存，使失效落空。因此写侧应在完成全部写库操作后调用本方法，
     * 将失效动作注册到事务提交后执行。
     * <p>
     * 当前不存在活跃的事务同步时（如被内部调用绕过了事务代理），退化为立即执行。
     *
     * @param action 缓存失效动作
     */
    public static void evictAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}
