package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目应用配置属性 —— 映射 {@code app.*} 顶层配置项。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 演示模式开关。
     * <p>
     * 开启后由 {@link cn.codesensi.amour.interceptor.DemoModeInterceptor} 拦截所有写操作请求；
     * 与 sys_config 表配置不同，配置文件改动需重启应用生效。
     * 默认值：{@code false}。
     */
    private Boolean demoMode = false;

}
