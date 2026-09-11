package cn.codesensi.amour.common.context;

import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {

    /**
     * 包装异步任务:提交时快照主线程 MDC,执行时恢复,结束后还原,
     * 保证异步日志沿用主线程的链路追踪 ID,并防止线程池复用导致上下文串扰。
     */
    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            if (context != null) {
                MDC.setContextMap(context);
            }
            try {
                runnable.run();
            } finally {
                if (previous != null) {
                    MDC.setContextMap(previous);
                } else {
                    MDC.clear();
                }
            }
        };
    }
}