package cn.codesensi.amour.common.consts;

/**
 * 线程常量 —— 线程池执行器的 Bean 名称定义。
 *
 * @author codesensi
 * @since 1.0
 */
public class ThreadConst {

    /**
     * 异步任务执行器名称
     */
    public static final String ASYNC_EXECUTOR_NAME = "asyncExecutor";

    /**
     * 操作日志专用执行器名称
     */
    public static final String LOG_EXECUTOR_NAME = "logExecutor";

    /**
     * 定时任务调度器名称（避免使用 Spring 内部约定名 taskScheduler，
     * 防止未来引入 EnableScheduling 时发生 Bean 语义冲突）
     */
    public static final String TASK_SCHEDULER_NAME = "JobTaskScheduler";
}
