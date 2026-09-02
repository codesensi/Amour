package cn.codesensi.amour.interceptor;

import cn.codesensi.amour.common.exception.AuthorizationException;
import cn.codesensi.amour.common.properties.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 演示模式拦截器。
 * <p>
 * 当演示模式开关（配置项 {@code app.demo-mode}）为 {@code true} 时，
 * 拦截所有 POST、PUT、DELETE 等写操作请求，并抛出 {@link AuthorizationException}，
 * 防止演示环境中的数据被非授权修改。
 * <p>
 * 开关来自配置文件而非 sys_config 表，修改后需重启应用生效。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoModeInterceptor implements HandlerInterceptor {

    private final AppProperties appProperties;

    /**
     * 演示模式写操作拦截。
     * <p>
     * 当 {@code app.demo-mode} 配置开启时，仅放行 GET、HEAD 等只读请求，
     * 其余写操作（POST、PUT、DELETE 等）抛出 {@link AuthorizationException}；
     * 演示模式未开启时直接放行所有请求。
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  实际执行的处理器
     * @return {@code true} 表示继续执行后续拦截器与处理器
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 读取演示模式开关（配置文件 app.demo-mode，默认 false）
        if (Boolean.TRUE.equals(appProperties.getDemoMode())) {
            String method = request.getMethod();
            if (!RequestMethod.GET.name().equals(method) && !RequestMethod.HEAD.name().equals(method)) {
                log.warn("演示模式拦截写操作：method={}，uri={}", method, request.getRequestURI());
                throw new AuthorizationException("演示模式不允许操作哦~");
            }
        }
        return true;
    }
}
