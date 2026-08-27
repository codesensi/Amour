package cn.codesensi.amour.context;

import cn.codesensi.amour.common.util.CacheUtil;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 应用运行环境上下文。
 * <p>
 * 在应用启动时从 Spring {@link Environment} 中读取项目名（{@code spring.application.name}）
 * 与当前激活的 Profile（运行环境），供 {@link CacheUtil} 等组件
 * 拼接「项目名_运行环境」前缀，实现多项目、多环境的隔离。
 * <p>
 * 该组件由 Spring 容器管理，构造时完成属性绑定；属性缺失时会回退到默认值
 * （项目名缺省为 {@code app}，Profile 缺省为 {@code default}）。
 */
@Data
@Component
public class AppEnvContext {

    /**
     * 由 Spring 装配后自持的静态实例，供 {@link CacheUtil} 等
     * 非 Spring 管理（未加 {@code @Component}）的静态工具类读取当前环境信息。
     * 构造器执行完成后即被赋值，此后不再变化。
     */
    private static AppEnvContext INSTANCE;

    /**
     * Spring 配置项 {@code spring.application.name} 的键名，用于读取项目名。
     */
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";

    /**
     * 项目名缺省值；当 {@code spring.application.name} 未配置时使用。
     */
    public static final String DEFAULT_APP_NAME = "app";

    /**
     * 运行环境缺省值；当未激活任何 Profile 时使用。
     */
    public static final String DEFAULT_ACTIVE_PROFILE = "default";

    /**
     * 项目名，来源于 {@code spring.application.name}，缺省为 {@link #DEFAULT_APP_NAME}。
     */
    private final String appName;

    /**
     * 当前激活的 Profile 列表（可能为空数组）。
     */
    private final String[] activeProfiles;

    /**
     * 已拼接好的激活 Profile 字符串（逗号分隔），便于日志打印；无 Profile 时为 {@code default}。
     */
    private final String activeProfileStr;

    /**
     * 第一个激活的 Profile，用于拼接缓存名等场景；无 Profile 时为 {@code default}。
     */
    private final String firstActiveProfile;

    /**
     * 通过 Spring {@link Environment} 初始化应用环境上下文。
     *
     * @param environment Spring 环境抽象，提供属性与 Profile 信息
     */
    public AppEnvContext(Environment environment) {
        // 属性缺失时给出合理默认值：取 spring.application.name，缺省为 "app"
        this.appName = environment.getProperty(SPRING_APPLICATION_NAME, DEFAULT_APP_NAME);
        this.activeProfiles = environment.getActiveProfiles();
        // 如果未激活任何Profile，Spring默认返回 ["default"]
        if (this.activeProfiles.length == 0) {
            this.activeProfileStr = DEFAULT_ACTIVE_PROFILE;
        } else {
            this.activeProfileStr = StringUtils.arrayToCommaDelimitedString(this.activeProfiles);
        }
        this.firstActiveProfile = this.activeProfiles.length > 0 ? this.activeProfiles[0] : DEFAULT_ACTIVE_PROFILE;
        // 暴露为静态实例，供非 Spring 管理的静态工具类读取
        AppEnvContext.INSTANCE = this;
    }

    /**
     * 获取由 Spring 装配完成的 {@link AppEnvContext} 静态实例。
     * <p>
     * 仅在应用启动、该组件被 Spring 实例化之后调用才有值；在 {@code @Bean} 方法参数
     * 或已注入该组件的地方调用可保证其已就绪，返回不为 null。
     *
     * @return 应用环境上下文的静态实例
     */
    public static AppEnvContext getInstance() {
        return INSTANCE;
    }

}
