package cn.codesensi.amour.interceptor;

import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.exception.AuthorizationException;
import cn.codesensi.amour.service.ConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 演示模式拦截器。
 * <p>
 * 当演示模式开关（{@link ConfigKeyEnum#DEMO_MODE}）配置为 {@code true} 时，
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

    /**
     * 演示模式写操作拦截。
     * <p>
     * 当 {@code demo-mode} 配置开启时，仅放行 GET、HEAD 等只读请求，
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
        // 读取演示模式开关（配置缺失/停用时结果为空列表，开关视为 false）
        boolean demoMode = configService.listByKeys(List.of(ConfigKeyEnum.DEMO_MODE.getCode())).stream()
                .anyMatch(config -> Boolean.parseBoolean(config.getConfigValue()));
        if (demoMode) {
            String method = request.getMethod();
            if (!RequestMethod.GET.name().equals(method) && !RequestMethod.HEAD.name().equals(method)) {
                throw new AuthorizationException("演示模式不允许操作哦~");
            }
        }
        return true;
    }
}
