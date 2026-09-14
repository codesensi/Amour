package cn.codesensi.amour.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 安全响应头过滤器 —— 全局统一追加基础防护头。
 * <p>
 * 响应头对浏览器而言是"默认安全策略"的开关，本过滤器对所有响应（含静态资源、文件流、错误页）
 * 无差别生效，构成纵深防御的兜底层：
 * <ul>
 *   <li>{@code X-Content-Type-Options: nosniff}：禁用 MIME 嗅探，禁止浏览器把
 *       text/plain 等响应"猜"成 HTML/JS 执行，与 /file/view 的服务端类型强推导形成双保险；</li>
 *   <li>{@code X-Frame-Options: SAMEORIGIN}：禁止被第三方站点 iframe 嵌套，关闭管理页的
 *       点击劫持攻击面（同源 iframe 不受影响）；</li>
 *   <li>{@code Referrer-Policy: strict-origin-when-cross-origin}：跨站跳转时 Referer
 *       只携带 origin，不泄露本站路径与查询参数（如 captchaKey）。</li>
 * </ul>
 * <p>
 * CSP（Content-Security-Policy）暂不加入：vue-pure-admin 构建产物存在内联样式/脚本，
 * 过严策略易误伤，待以 report-only 模式灰度观察后再收紧。
 *
 * @author codesensi
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    /**
     * 禁用 MIME 嗅探的响应头名称。
     */
    private static final String HEADER_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";

    /**
     * 禁止 iframe 嵌套的响应头名称。
     */
    private static final String HEADER_FRAME_OPTIONS = "X-Frame-Options";

    /**
     * 引用页策略的响应头名称。
     */
    private static final String HEADER_REFERRER_POLICY = "Referrer-Policy";

    /**
     * 对每个 HTTP 请求追加安全响应头。
     * <p>
     * 头在放行请求前设置，确保后续任何处理分支（含异常响应）均已携带；
     * 与 {@link TraceIdFilter} 的先后顺序无耦合（仅设置响应头，不读取链上产物）。
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
        // 防浏览器 MIME 嗅探(配合 /file/view 的服务端类型推导,双保险)
        response.setHeader(HEADER_CONTENT_TYPE_OPTIONS, "nosniff");
        // 禁止被第三方页面 iframe 嵌套(同源 iframe 不受影响)
        response.setHeader(HEADER_FRAME_OPTIONS, "SAMEORIGIN");
        // 外链跳转仅携带 origin,不泄露本站路径与查询参数
        response.setHeader(HEADER_REFERRER_POLICY, "strict-origin-when-cross-origin");
        filterChain.doFilter(request, response);
    }

}
