package cn.codesensi.amour.common.context;

import cn.codesensi.amour.common.util.LoginUserUtil;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * 异步任务上下文装饰器 —— 提交时快照主线程的 MDC 链路与登录用户ID，
 * 执行时恢复到任务线程，结束后还原线程既有状态：既保证异步日志沿用主线程的
 * 链路追踪 ID，也使审计字段填充在异步线程中仍能取到提交者身份（见 {@link LoginUserUtil}），
 * 并防止线程池复用导致上下文串扰。
 */
public class ContextTaskDecorator implements TaskDecorator {

    /**
     * 包装异步任务:提交时快照主线程的 MDC 与登录用户ID,执行时恢复,结束后还原线程既有状态,
     * 防止线程池复用导致上下文串扰。
     */
    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        // 快照提交者身份（嵌套提交时此处取到的即是外层任务绑定的值,天然级联）
        Long loginId = LoginUserUtil.getLoginIdOrNull();
        return () -> {
            Map<String, String> previousMdc = MDC.getCopyOfContextMap();
            Long previousLoginId = LoginUserUtil.getBoundId();
            if (context != null) {
                MDC.setContextMap(context);
            }
            LoginUserUtil.bindLoginId(loginId);
            try {
                runnable.run();
            } finally {
                if (previousMdc != null) {
                    MDC.setContextMap(previousMdc);
                } else {
                    MDC.clear();
                }
                LoginUserUtil.bindLoginId(previousLoginId);
            }
        };
    }
}
