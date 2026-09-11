package cn.codesensi.amour.config;

import cn.codesensi.amour.common.consts.ThreadConst;
import cn.codesensi.amour.common.context.MdcTaskDecorator;
import cn.codesensi.amour.common.properties.ThreadPoolProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置 —— 注册两个语义化执行器：
 * <ul>
 *   <li>{@code asyncExecutor}（通用池）：承载操作日志之外的其他异步任务，
 *       支持虚拟线程/平台线程两种模式（{@code thread.pool.virtual-threads}），
 *       饱和时反压（CallerRuns / 并发限流），任务不丢；</li>
 *   <li>{@code logExecutor}（日志池）：承载操作日志的异步落库
 *       （{@code SysLogService#record}），队列饱和即丢弃并告警，
 *       绝不允许阻塞或反压业务线程。</li>
 * </ul>
 * 两池均通过 {@link MdcTaskDecorator} 沿用提交线程的 MDC 上下文（链路追踪 ID）。
 *
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {

    private final ThreadPoolProperties threadPoolProperties;

    /**
     * 调用者运行并告警策略：先输出池饱和告警，再由提交线程直接执行被拒绝的任务（反压，任务不丢），
     * 通用池的设计默认策略。
     */
    private static final RejectedExecutionHandler CALLER_RUNS_WARN_HANDLER = (r, pool) -> {
        log.warn("线程池已饱和，任务转由提交线程执行(CallerRuns): queueSize={}", pool.getQueue().size());
        if (!pool.isShutdown()) {
            r.run();
        }
    };

    /**
     * 丢弃并告警策略：丢弃被拒绝的任务并输出告警日志（丢弃可见），日志池的设计默认策略。
     */
    private static final RejectedExecutionHandler DISCARD_WARN_HANDLER = (r, pool) ->
            log.warn("异步任务被丢弃(线程池饱和): queueSize={}", pool.getQueue().size());

    /**
     * 拒绝策略名称与处理器实例的映射表。
     * <p>
     * Key 与配置文件 {@code thread.pool.rejected-execution-handler} 项的值对应，
     * 支持以下策略：
     * <ul>
     *   <li>{@code CallerRunsPolicy} — 调用线程直接执行该任务（通过降低任务提交速率实现背压）；</li>
     *   <li>{@code CallerRunsWarnPolicy} — 先输出池饱和告警，再由调用线程执行该任务（CallerRuns + 告警）；</li>
     *   <li>{@code AbortPolicy} — 直接抛出 {@link RejectedExecutionException}（最激进，明确告知拒绝）；</li>
     *   <li>{@code DiscardPolicy} — 静默丢弃当前被拒绝的任务，任务丢失不可见；</li>
     *   <li>{@code DiscardOldestPolicy} — 丢弃队列头部的等待任务（最旧的），然后重试提交当前任务。</li>
     *   <li>{@code DiscardWarnPolicy} — 丢弃当前被拒绝的任务并输出告警日志（丢弃可见）。</li>
     * </ul>
     */
    private static final Map<String, RejectedExecutionHandler> REJECTED_HANDLER_MAP = Map.of(
            "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy(),
            "CallerRunsWarnPolicy", CALLER_RUNS_WARN_HANDLER,
            "AbortPolicy", new ThreadPoolExecutor.AbortPolicy(),
            "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy(),
            "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy(),
            "DiscardWarnPolicy", DISCARD_WARN_HANDLER
    );

    /**
     * 异步任务未捕获异常的全局处理：void 返回的 {@code @Async} 方法抛出的异常
     * 在此统一按项目日志格式记录告警，避免默认处理器丢失上下文。
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("异步任务未捕获异常: method={}", method.getName(), ex);
    }

    /**
     * 通用异步任务执行器，Bean 名与 {@link ThreadConst#ASYNC_EXECUTOR_NAME} 对应，
     * 并标注 {@code @Primary} 作为未指定限定符的 {@code @Async} 任务的默认执行器。
     * <p>
     * 支持两种模式（{@code thread.pool.general.virtual-threads}）：
     * <ul>
     *   <li>{@code true} —— 虚拟线程：每任务一虚拟线程，仅 {@code max-pool-size} 生效（复用为并发上限），
     *       队列/保活参数忽略，IO 密集型任务近乎零成本并发；</li>
     *   <li>{@code false} —— 平台线程池：core/max/queue/keepAlive 全参数生效。</li>
     * </ul>
     * 拒绝策略由配置文件 {@code thread.pool.general.rejected-execution-handler} 决定
     * （当前为 CallerRunsWarnPolicy：队列饱和时告警并由提交线程执行，任务不丢）。
     *
     * @return 通用异步任务执行器
     */
    @Primary
    @Bean(ThreadConst.ASYNC_EXECUTOR_NAME)
    public AsyncTaskExecutor asyncExecutor() {
        ThreadPoolProperties.Pool spec = threadPoolProperties.getGeneral();
        if (Boolean.TRUE.equals(spec.getVirtualThreads())) {
            return virtualExecutor();
        }
        return buildExecutor(spec, "async-thread-",
                resolveHandler(spec.getRejectedExecutionHandler(), CALLER_RUNS_WARN_HANDLER));
    }

    /**
     * 操作日志专用执行器 —— 小规格旁路池，队列饱和即丢弃并告警，绝不反压业务线程。
     * <p>
     * 固定平台线程池：虚拟线程永不拒绝任务，会使丢弃语义失效；与通用池隔离，
     * 日志任务的丢弃不影响通用任务的 CallerRuns 反压语义。
     *
     * @return 操作日志专用异步执行器
     */
    @Bean(ThreadConst.LOG_EXECUTOR_NAME)
    public ThreadPoolTaskExecutor logExecutor() {
        ThreadPoolProperties.Pool spec = threadPoolProperties.getLog();
        return buildExecutor(spec, "log-async-",
                resolveHandler(spec.getRejectedExecutionHandler(), DISCARD_WARN_HANDLER));
    }

    /**
     * 按规格构建平台线程池执行器：统一装配参数、MDC 装饰器与初始化，两池共用。
     *
     * @param spec       池规格
     * @param namePrefix 线程名前缀
     * @param handler    拒绝策略处理器
     * @return 平台线程池执行器
     */
    private ThreadPoolTaskExecutor buildExecutor(ThreadPoolProperties.Pool spec, String namePrefix,
                                                 RejectedExecutionHandler handler) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(spec.getCorePoolSize());
        executor.setMaxPoolSize(spec.getMaxPoolSize());
        executor.setQueueCapacity(spec.getQueueCapacity());
        executor.setKeepAliveSeconds(spec.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(spec.getAllowCoreThreadTimeout());
        executor.setWaitForTasksToCompleteOnShutdown(spec.getWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(spec.getAwaitTerminationSeconds());
        executor.setThreadNamePrefix(namePrefix);
        executor.setRejectedExecutionHandler(handler);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * 构建虚拟线程执行器：每任务一虚拟线程，并发上限复用 {@code max-pool-size} 配置，
     * 并发达到上限时提交线程阻塞等待（反压，任务不丢）。
     *
     * @return 虚拟线程执行器
     */
    private SimpleAsyncTaskExecutor virtualExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("async-vt-");
        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(threadPoolProperties.getGeneral().getMaxPoolSize());
        executor.setTaskDecorator(new MdcTaskDecorator());
        return executor;
    }


    /**
     * 按配置名解析拒绝策略；名称缺失或非法时回退为调用方给定的默认策略并输出告警
     * （通用池回退 CallerRunsPolicy、日志池回退 DiscardWarnPolicy，与各自设计语义一致）。
     *
     * @param name     拒绝策略名称
     * @param fallback 配置缺失或非法时的兜底策略
     * @return 对应的拒绝策略处理器
     */
    private RejectedExecutionHandler resolveHandler(String name, RejectedExecutionHandler fallback) {
        RejectedExecutionHandler handler = name == null ? null : REJECTED_HANDLER_MAP.get(name);
        if (handler == null) {
            log.warn("未知的拒绝策略配置：{}，回退为调用方默认策略", name);
            return fallback;
        }
        return handler;
    }
}
