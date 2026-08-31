package cn.codesensi.amour.filter;

import cn.codesensi.amour.common.properties.AppSecurityProperties;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 请求体缓存过滤器 —— 将原始请求包装为 {@link ContentCachingRequestWrapper}，缓存请求体内容。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    private final AppSecurityProperties appSecurityProperties;

    /**
     * 将请求包装为 {@link ContentCachingRequestWrapper}，缓存请求体以便重复读取。
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>如果是 {@code multipart/form-data} 请求（文件上传），跳过包装直接放行，
     *       避免大文件占用内存；</li>
     *   <li>其他请求用 {@link ContentCachingRequestWrapper} 包装后传递到过滤器链下游。</li>
     * </ol>
     *
     * @param request     原始 HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 文件上传请求跳过包装，避免大文件缓存导致内存溢出
        if (StrUtil.containsIgnoreCase(request.getContentType(), "multipart/form-data")) {
            log.debug("multipart 请求跳过请求体缓存：uri={}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        // 将原始请求包装为可缓存请求体的包装器，传递给后续过滤器
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, appSecurityProperties.getRequestCacheLimit());
        filterChain.doFilter(requestWrapper, response);
    }
}
