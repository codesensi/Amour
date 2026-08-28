package cn.codesensi.amour.config;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.interceptor.DemoModeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
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
        // registry.addInterceptor(new SaInterceptor(handler -> {
        //             // 所有请求（排除 swagger 等公开路径）需登录 + 账号未封禁
        //             SaRouter.match(RbacConst.ROOT_PATH)
        //                     .notMatch(RbacConst.SWAGGER_PATH)
        //                     .check(r -> {
        //                         StpUtil.checkLogin();
        //                         StpUtil.checkDisable(StpUtil.getLoginIdAsLong());
        //                     });
        //             // 系统管理类接口（/sys/**、/log/**、/conf/**）需 superadmin 角色
        //             SaRouter.match(RbacConst.SYS_PATH, RbacConst.LOG_PATH, RbacConst.CONF_PATH)
        //                     // 排除用户基本信息接口（登录后即可访问）
        //                     .notMatch(RbacConst.SYS_USER_INFO_PATH)
        //                     .check(r -> StpUtil.checkRole(RbacConst.ROLE_ADMIN_CODE));
        //         })).addPathPatterns(RbacConst.ROOT_PATH)
        //         .order(1);

        // 2. 演示模式拦截器：演示环境下仅允许查询和登录/登出，拒绝所有写操作
        registry.addInterceptor(demoModeInterceptor)
                .addPathPatterns(RbacConst.ROOT_PATH)
                // 获取验证码、登录、登出接口不受演示模式限制
                .excludePathPatterns(RbacConst.CAPTCHA_PATH, RbacConst.LOGIN_PATH, RbacConst.LOGOUT_PATH)
                .order(2);
    }

    /**
     * 注册页面跳转视图控制器 —— 无业务逻辑的纯跳转路由。
     *
     * <p>门户页已由静态 HTML 迁移为 Thymeleaf 模板（公共外壳见 templates/fragments/layout.html），
     * 此处以视图控制器直连模板渲染，渲染产物与原静态页等价，pjax 局部刷新行为不变。</p>
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 前端展示首页：/ 与 /index.html 均渲染首页模板（旧 .html 链接保持可用）
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        // 门户子页：点点滴滴 / 留言板 / 关于我们 / 恋爱相册 / 恋爱列表
        registry.addViewController("/portal/moments.html").setViewName("portal/moments");
        registry.addViewController("/portal/message.html").setViewName("portal/message");
        registry.addViewController("/portal/about.html").setViewName("portal/about");
        registry.addViewController("/portal/love-photo.html").setViewName("portal/love-photo");
        registry.addViewController("/portal/love-list.html").setViewName("portal/love-list");
        // 后台管理入口：/admin 跳转到后台管理登录页（仍为静态页）
        registry.addRedirectViewController("/admin", "/admin/login.html");
    }

    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    // @Bean
    // public StpLogic getStpLogicJwt() {
    //     return new StpLogicJwtForSimple();
    // }

}
