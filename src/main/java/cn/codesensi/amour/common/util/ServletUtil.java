package cn.codesensi.amour.common.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * Servlet 工具类 —— 基于当前请求上下文（{@link RequestContextHolder}）便捷获取
 * request、response、session、请求参数，以及浏览器 UA 与 URL 编解码能力。
 * <p>
 * 注意：除 {@link #getParameterMap(ServletRequest)} 外，其余方法均依赖当前线程
 * 绑定的请求上下文，只能在 Web 请求线程内调用。
 */
public class ServletUtil {

    /**
     * 浏览器 User-Agent 请求头的键名。
     */
    private static final String USER_AGENT_KEY = "User-Agent";

    /**
     * 获取当前线程绑定的 request。
     *
     * @return 当前 HTTP 请求对象
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取当前线程绑定的请求属性（含 request 与 response）。
     *
     * @return 当前请求属性；非 Web 线程中调用时可能返回 {@code null}
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取当前请求的浏览器 UA。
     *
     * @return User-Agent 请求头的值
     */
    public static String getUserAgent() {
        return getUserAgent(getRequestAttributes().getRequest());
    }

    /**
     * 获取指定请求的浏览器 UA。
     *
     * @param request HTTP 请求
     * @return User-Agent 请求头的值；未携带时返回 {@code null}
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT_KEY);
    }

    /**
     * 获取当前请求的指定参数值。
     *
     * @param name 参数名
     * @return 参数值；参数不存在时返回 {@code null}
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获得指定请求的所有请求参数（只读视图）。
     *
     * @param request Servlet 请求
     * @return 参数 Map（key=参数名，value=参数值数组），不可修改
     */
    public static Map<String, String[]> getParameterMap(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }


    /**
     * 获取当前线程绑定的 response。
     *
     * @return 当前 HTTP 响应对象
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取当前请求的 session；不存在时自动创建。
     *
     * @return 当前 HTTP 会话
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }


    /**
     * 按 UTF-8 进行 URL 编码。
     *
     * @param str 待编码内容
     * @return 编码后的字符串
     */
    public static String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    /**
     * 按 UTF-8 进行 URL 解码。
     *
     * @param str 待解码内容
     * @return 解码后的字符串
     */
    public static String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

}
