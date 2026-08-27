package cn.codesensi.amour.interceptor;

import cn.codesensi.amour.common.exception.AuthorizationException;
import cn.codesensi.amour.service.ConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 演示模式拦截器。
 * <p>
 * 当 {@link ConfigService#getBool(String) demo-mode} 配置为 {@code true} 时，
 * 拦截所有 POST、PUT、DELETE 等写操作请求，并抛出 {@link AuthorizationException}，
 * 防止演示环境中的数据被非授权修改。
 *
 * @author codesensi
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DemoModeInterceptor implements HandlerInterceptor {

    private final ConfigService configService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (configService.getBool("demo-mode")) {
            String method = request.getMethod();
            if (!RequestMethod.GET.name().equals(method) && !RequestMethod.HEAD.name().equals(method)) {
                throw new AuthorizationException("演示模式不允许操作哦~");
            }
        }
        return true;
    }
}
