package cn.codesensi.amour.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 请求工具类 —— 提供请求体读取和请求头获取的便捷方法。
 *
 * @author codesensi
 * @since 1.0
 */
public class RequestUtil {

    /**
     * 获取 HTTP 请求的请求体内容（JSON 字符串）。
     * <p>
     * 仅当请求被 {@link ContentCachingRequestWrapper} 包装过时才能读取到缓存内容，
     * 否则返回 {@code null}。如果请求体为空字节数组（如 GET 请求），也返回 {@code null}。
     *
     * @param request HTTP 请求
     * @return 请求体字符串，不可用时返回 {@code null}
     */
    public static String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                try {
                    return new String(content, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return new String(content, StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }

    /**
     * 获取指定的请求头值。
     *
     * @param request    HTTP 请求
     * @param headerName 请求头名称（不区分大小写）
     * @return 请求头值，请求或头名称为 {@code null} 时返回 {@code null}
     */
    public static String getHeader(HttpServletRequest request, String headerName) {
        if (request == null || headerName == null) {
            return null;
        }
        return request.getHeader(headerName);
    }

    /**
     * 获取所有请求头（单值版本）。
     * <p>
     * 每个请求头名对应一个值，如果某请求头有多个值，只返回第一个。
     * 如需获取所有值，请使用 {@link #getAllHeadersMultiValue(HttpServletRequest)}。
     *
     * @param request HTTP 请求
     * @return 请求头 Map（key=头名称，value=头值），请求为 {@code null} 时返回空 Map
     */
    public static Map<String, String> getAllHeaders(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                headerMap.put(name, value);
            }
        }
        return headerMap;
    }

    /**
     * 获取所有请求头（多值版本）。
     * <p>
     * 每个请求头名对应一个字符串数组，适用于同名的请求头有多个值的情况
     * （如 {@code Accept}、{@code Set-Cookie} 等）。
     *
     * @param request HTTP 请求
     * @return 请求头 Map（key=头名称，value=头值数组），请求为 {@code null} 时返回空 Map
     */
    public static Map<String, String[]> getAllHeadersMultiValue(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, String[]> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                // 获取该请求头名的所有值
                Enumeration<String> values = request.getHeaders(name);
                if (values != null) {
                    java.util.List<String> list = Collections.list(values);
                    headerMap.put(name, list.toArray(new String[0]));
                }
            }
        }
        return headerMap;
    }
}
