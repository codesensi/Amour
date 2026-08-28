package cn.codesensi.amour.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * 前端资源 URL 拦截器 —— 为门户视图渲染注入 ResourceUrlProvider。
 *
 * <p>配合 WebMvcConfig 中启用的内容指纹版本策略（VersionResourceResolver），
 * 门户模板通过 <code>${resourceUrlProvider.getForLookup('/assets/...')}</code>
 * 生成形如 xxx-{内容hash}.css 的指纹 URL，实现静态资源缓存的全自动失效。</p>
 *
 * <p>注入说明：以 ObjectProvider 延迟获取，避免 WebMvcConfig 与
 * ResourceUrlProvider 之间的循环依赖；之所以用拦截器注入而非模板内 @bean 表达式，
 * 是因为 Thymeleaf 3.1 默认禁止模板 SpEL 直接访问 Spring Bean；
 * 之所以不用 @ControllerAdvice，是因为门户页由视图控制器直连渲染，不触发 ModelAttribute 回调。</p>
 *
 * @author codesensi
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class FrontendResourceInterceptor implements HandlerInterceptor {

    private final ObjectProvider<ResourceUrlProvider> resourceUrlProvider;

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           ModelAndView modelAndView) {
        // 仅对正常渲染视图的请求注入；重定向或无视图请求跳过
        if (modelAndView != null && modelAndView.getViewName() != null
                && !modelAndView.getViewName().startsWith("redirect:")) {
            ResourceUrlProvider provider = resourceUrlProvider.getIfAvailable();
            if (provider != null) {
                modelAndView.addObject("resourceUrlProvider", provider);
            }
        }
    }

}
