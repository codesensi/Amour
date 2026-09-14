package cn.codesensi.amour.common.util;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.context.AppEnvContext;
import cn.hutool.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Function;

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
 *
 * @since 1.0
 */
@Slf4j
public class CacheUtil {

    /**
     * 为给定缓存名拼接「项目名_运行环境」前缀。
     * <p>
     * 例如 {@code withAppEnv("captcha")} 在项目名 {@code amour}、环境 {@code dev} 时返回
     * {@code amour_dev_captcha}。
     *
     * @param cacheName 基础缓存名，不可为 null
     * @return 拼接后的缓存名
     * @throws IllegalStateException 应用上下文尚未由 Spring 装配完成时抛出
     */
    public static String withAppEnv(String cacheName) {
        AppEnvContext ctx = AppEnvContext.getInstance();
        if (ObjUtil.isNull(ctx)) {
            // 实例就绪前调用属于编程错误，给出明确报错而非 NPE
            throw new IllegalStateException("AppEnvContext 尚未由 Spring 装配完成，无法拼接带环境前缀的缓存名");
        }
        return ctx.getAppName() + AppConst.UNDERSCORE + ctx.getFirstActiveProfile() + AppConst.UNDERSCORE + cacheName;
    }

    /**
     * 从指定缓存读取数据；未命中时原子回源并回填，缓存未注册或回源异常时降级为直接加载。
     * <p>
     * 统一收口项目内缓存读取的三个要素，避免各服务重复实现导致口径漂移：
     * <ul>
     *     <li>缓存未注册/未就绪：降级为直接执行 loader（不写缓存）；</li>
     *     <li>通过 {@link Cache#get(Object, Callable)} 原子回源，未命中时查库并写入，防止并发击穿；</li>
     *     <li>回源异常（如缓存故障）：降级为直接执行 loader，不阻断业务读取。</li>
     * </ul>
     * loader 返回 {@code null} 时以 {@link CacheConst#NULL_MARKER} 哨兵占位（Caffeine 不允许缓存
     * {@code null}），读取时还原为 {@code null}，使"数据不存在"的结果也被缓存以防空值穿透。
     *
     * @param cacheManager 缓存管理器
     * @param cacheName    基础缓存名（内部自动拼接「项目名_运行环境」前缀）
     * @param key          缓存键
     * @param loader       回源加载函数
     * @param <K>          键类型
     * @param <T>          值类型
     * @return 缓存或回源得到的值；数据不存在时为 {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <K, T> T load(CacheManager cacheManager, String cacheName, K key, Function<K, T> loader) {
        Cache cache = cacheManager.getCache(withAppEnv(cacheName));
        if (ObjUtil.isNull(cache)) {
            // 缓存未注册/未就绪：降级为直接回源
            log.debug("{} 缓存未注册，降级为直接查库：key={}", cacheName, key);
            return loader.apply(key);
        }
        try {
            // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿；null 以空值哨兵占位防穿透
            Object cached = cache.get(key, () -> {
                T value = loader.apply(key);
                return ObjUtil.defaultIfNull(value, CacheConst.NULL_MARKER);
            });
            return cached == CacheConst.NULL_MARKER ? null : (T) cached;
        } catch (Cache.ValueRetrievalException e) {
            // 回源异常时降级为直接回源，避免缓存故障阻断业务读取
            log.debug("{} 缓存回源异常，降级为直接查库：key={}", cacheName, key, e);
            return loader.apply(key);
        }
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
            log.debug("存在活跃事务，缓存失效动作注册到事务提交后执行");
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            log.debug("无活跃事务同步，立即执行缓存失效动作");
            action.run();
        }
    }
}
