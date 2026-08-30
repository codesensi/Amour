package cn.codesensi.amour.config;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.interceptor.DemoModeInterceptor;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 拦截器链配置 —— 统一注册应用的拦截器与页面跳转视图控制器。
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
     * 注册应用级拦截器链。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. SaToken 鉴权拦截器：初始化 SaTokenContext + 登录校验 + 封禁校验 + 角色校验
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 所有请求需登录 + 账号未封禁
                    SaRouter.match(RbacConst.ROOT_PATH)
                            .check(r -> {
                                StpUtil.checkLogin();
                                StpUtil.checkDisable(StpUtil.getLoginIdAsLong());
                            });
                })).addPathPatterns(RbacConst.ROOT_PATH)
                .order(1);

        // 2. 演示模式拦截器：演示环境下仅允许查询和登录/登出，拒绝所有写操作
        registry.addInterceptor(demoModeInterceptor)
                .addPathPatterns(RbacConst.ROOT_PATH)
                // 获取验证码、登录、登出接口不受演示模式限制
                .excludePathPatterns(RbacConst.CAPTCHA_PATH, RbacConst.LOGIN_PATH, RbacConst.LOGOUT_PATH)
                .order(2);
    }

    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
