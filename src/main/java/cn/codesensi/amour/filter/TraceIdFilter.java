package cn.codesensi.amour.filter;

import cn.codesensi.amour.common.consts.AppConst;
import cn.hutool.core.util.IdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 链路追踪 ID（TraceId）过滤器。
 * <p>
 * 继承 {@link OncePerRequestFilter}，保证同一请求只执行一次：ASYNC dispatch 重入时
 * 不会重复生成新的 traceId，也不会提前清理首次注入的上下文。
 *
 * @author codesensi
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceIdFilter extends OncePerRequestFilter {

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
     * @param request     当前请求
     * @param response    当前响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 将 traceId 放入 MDC 上下文
            MDC.put(AppConst.TRACE_ID, IdUtil.fastSimpleUUID());
            filterChain.doFilter(request, response);
        } finally {
            // 确保清理，避免线程复用导致上下文污染
            MDC.remove(AppConst.TRACE_ID);
        }
    }

}
