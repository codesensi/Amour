package cn.codesensi.amour.config;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.interceptor.DemoModeInterceptor;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 拦截器链配置 —— 统一注册应用的拦截器。
 * <p>
 * 拦截链按 order 从小到大执行：鉴权（order 1）→ 演示模式（order 2）。
 * 两个拦截器共享同一份公开路径清单（{@link RbacConst#PUBLIC_PATHS}：验证码、登录、登出、
 * 门户 /portal/**、文件预览），调整公开口径时仅需维护该常量；
 * 例外：H2 控制台（/h2-console/**，仅 dev 启用）仅从鉴权拦截器豁免——
 * 演示模式拦截器不豁免它，保留演示开关对控制台写操作的拦截能力；
 * 鉴权拦截器还对非 Controller 处理器（静态资源、404 兜底）不做登录校验。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final DemoModeInterceptor demoModeInterceptor;

    /**
     * 注册应用级拦截器链：未登录请求在鉴权拦截器即被拒绝，
     * 通过鉴权的请求再经演示模式拦截器做写操作限制（演示开关开启时）。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. SaToken 鉴权拦截器:初始化 SaTokenContext + 登录校验 + 封禁校验 + 注解鉴权(@SaCheckPermission)
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 仅对 Controller 方法做登录/封禁校验;
                    // 静态资源(favicon 等)与未命中路径的 404 兜底由 ResourceHttpRequestHandler 接管,
                    // 属于浏览器自动发起的附带请求,不校验登录,避免产生授权异常日志噪音
                    if (!(handler instanceof HandlerMethod)) {
                        return;
                    }
                    StpUtil.checkLogin();
                    StpUtil.checkDisable(StpUtil.getLoginIdAsLong());
                })).addPathPatterns(RbacConst.ROOT_PATH)
                // 公开路径见 RbacConst.PUBLIC_PATHS;
                // H2 控制台仅 dev 启用且自带 JDBC 账密页,免登录放行(演示模式拦截器不豁免,其写操作仍受限)
                .excludePathPatterns(RbacConst.PUBLIC_PATHS)
                .excludePathPatterns(RbacConst.H2_CONSOLE_PATH)
                .order(1);

        // 2. 演示模式拦截器：演示开关(app.demo-mode)开启时仅放行 GET/HEAD 等只读请求,
        //    拒绝 POST/PUT/DELETE 等写操作;公开路径(PUBLIC_PATHS)豁免
        registry.addInterceptor(demoModeInterceptor)
                .addPathPatterns(RbacConst.ROOT_PATH)
                .excludePathPatterns(RbacConst.PUBLIC_PATHS)
                .order(2);
    }

    /**
     * Sa-Token 整合 JWT(Simple 简单模式)。
     * <p>
     * 保留 sa-token 原生会话与注解鉴权能力，仅将令牌替换为 JWT 风格签名串，
     * 使会话信息可被无状态校验，便于后续水平扩展。
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}