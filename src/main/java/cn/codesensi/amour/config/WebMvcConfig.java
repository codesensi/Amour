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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 拦截器链配置 —— 统一注册应用的拦截器。
 * <p>
 * 拦截链按 order 从小到大执行：鉴权（order 1）→ 演示模式（order 2）。
 * 两个拦截器共享同一份公开路径清单（验证码、登录、登出、门户 /portal/**），
 * 调整公开口径时需同步维护两处排除项。
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
                    // 所有请求需登录 + 账号未封禁(路径筛选由 addPathPatterns/excludePathPatterns 完成)
                    StpUtil.checkLogin();
                    StpUtil.checkDisable(StpUtil.getLoginIdAsLong());
                })).addPathPatterns(RbacConst.ROOT_PATH)
                // 公开路径:验证码、登录、登出为幂等公开接口;
                // 门户端接口(/portal/**)面向访客免登录,统一放行——新增门户接口时无需再加 @SaIgnore,
                // 未实现的蓝图路径由此穿透到 Spring 层返回 404(前端门户空态承接)
                .excludePathPatterns(RbacConst.CAPTCHA_PATH,
                        RbacConst.LOGIN_PATH,
                        RbacConst.LOGOUT_PATH,
                        RbacConst.PORTAL_PATH)
                .order(1);

        // 2. 演示模式拦截器：演示开关(app.demo-mode)开启时仅放行 GET/HEAD 等只读请求,
        //    拒绝 POST/PUT/DELETE 等写操作;门户接口与验证码、登录、登出一同豁免
        registry.addInterceptor(demoModeInterceptor)
                .addPathPatterns(RbacConst.ROOT_PATH)
                .excludePathPatterns(RbacConst.CAPTCHA_PATH,
                        RbacConst.LOGIN_PATH,
                        RbacConst.LOGOUT_PATH,
                        RbacConst.PORTAL_PATH)
                .order(2);
    }

    /**
     * Sa-Token 整合 JWT(Simple 简单模式)。
     * <p>
     * 保留 sa-token 原生会话与注解鉴权能力,仅将令牌替换为 JWT 风格签名串,
     * 使会话信息可被无状态校验,便于后续水平扩展。
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
