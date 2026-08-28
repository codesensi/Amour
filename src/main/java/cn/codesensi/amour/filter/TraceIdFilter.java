package cn.codesensi.amour.filter;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.util.IdUtil;
import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 链路追踪 ID（TraceId）过滤器。
 *
 * @author codesensi
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceIdFilter implements Filter {

    /**
     * 对每个 HTTP 请求执行链路追踪 ID 的注入与清理。
     * <p>
     * 处理流程：
     * <ol>
     *   <li>生成 traceId</li>
     *   <li>将 traceId 放入 MDC 上下文；</li>
     *   <li>放行请求，执行后续过滤器及业务逻辑；</li>
     *   <li>在 {@code finally} 块中清理 MDC，避免线程上下文污染。</li>
     * </ol>
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param chain    过滤器链
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            // 将 traceId 放入 MDC 上下文
            MDC.put(AppConst.TRACE_ID, IdUtil.fastSimpleUUID());
            chain.doFilter(request, response);
        } finally {
            // 确保清理，避免线程复用导致上下文污染
            MDC.remove(AppConst.TRACE_ID);
        }
    }

}
