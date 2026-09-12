package cn.codesensi.amour.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servlet 工具类 —— 基于当前请求上下文（{@link RequestContextHolder}）便捷获取当前请求。
 * <p>
 * 注意：仅在 Web 请求线程内调用；非 Web 线程（如异步任务）中无绑定的请求上下文，
 * {@link #getRequest()} 返回 {@code null}，调用方需自行判空。
 *
 * @since 1.0
 */
public class ServletUtil {

    /**
     * 获取当前线程绑定的 request。
     * <p>
     * 非 Web 请求线程中调用（无请求上下文）时返回 {@code null}，调用方需自行判空。
     *
     * @return 当前 HTTP 请求对象；非 Web 线程中返回 {@code null}
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    /**
     * 获取当前线程绑定的请求属性（含 request 与 response）。
     *
     * @return 当前请求属性；非 Web 线程中调用时返回 {@code null}
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

}
