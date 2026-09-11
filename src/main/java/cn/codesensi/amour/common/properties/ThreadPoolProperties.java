package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池配置：通用池与日志池共用同一套规格结构，两池均在代码内带缺省值，yml 可覆盖。
 */
@Data
@Component
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolProperties {

    /**
     * 通用池配置（缺省值 5/20/100 + CallerRunsWarn，漏配也能启动）
     */
    private Pool general = new Pool(5, 20, 100, 30, true, true, 5, "CallerRunsWarnPolicy", true);

    /**
     * 日志池配置（缺省值即 1/2/200 + 丢弃告警）
     */
    private Pool log = new Pool(1, 2, 200, 60, false, true, 5, "DiscardWarnPolicy", false);

    /**
     * 池规格 —— 通用池与日志池共用的参数集
     */
    @Data
    public static class Pool {

        /**
         * 核心线程数
         */
        private Integer corePoolSize;

        /**
         * 最大线程数
         */
        private Integer maxPoolSize;

        /**
         * 缓冲队列大小
         */
        private Integer queueCapacity;

        /**
         * 线程的最大空闲秒数
         */
        private Integer keepAliveSeconds;

        /**
         * 是否允许核心线程超时
         */
        private Boolean allowCoreThreadTimeout;

        /**
         * 是否等待剩余任务完成后才关闭应用
         */
        private Boolean waitForTasksToCompleteOnShutdown;

        /**
         * 等待剩余任务完成的最大秒数
         */
        private Integer awaitTerminationSeconds;

        /**
         * 拒绝策略（CallerRunsPolicy / CallerRunsWarnPolicy / AbortPolicy / DiscardPolicy /
         * DiscardOldestPolicy / DiscardWarnPolicy）
         */
        private String rejectedExecutionHandler;

        /**
         * 是否启用虚拟线程(Java 21)：启用后本池其余平台线程参数被忽略，仅 max-pool-size 生效（复用为并发上限）。
         * 仅通用池支持；日志池固定平台线程池——虚拟线程永不拒绝任务，会使日志池的丢弃语义失效。
         */
        private Boolean virtualThreads;

        /**
         * Spring Boot 属性绑定所需的无参构造器
         */
        public Pool() {
        }

        /**
         * 按全量参数构建池规格（用于两池各自的代码内缺省值）
         */
        public Pool(Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity, Integer keepAliveSeconds,
                    Boolean allowCoreThreadTimeout, Boolean waitForTasksToCompleteOnShutdown,
                    Integer awaitTerminationSeconds, String rejectedExecutionHandler, Boolean virtualThreads) {
            this.corePoolSize = corePoolSize;
            this.maxPoolSize = maxPoolSize;
            this.queueCapacity = queueCapacity;
            this.keepAliveSeconds = keepAliveSeconds;
            this.allowCoreThreadTimeout = allowCoreThreadTimeout;
            this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
            this.awaitTerminationSeconds = awaitTerminationSeconds;
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            this.virtualThreads = virtualThreads;
        }
    }
}
