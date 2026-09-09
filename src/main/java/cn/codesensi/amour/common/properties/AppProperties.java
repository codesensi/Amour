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

    /**
     * QQ 信息上游接口地址模板（{@code %s} 为 URL 编码后的 QQ 号）。
     * <p>
     * 留空或未配置时 /qq-info 不调用上游，直接降级 avatar-api 拼接头像；
     * 配置文件改动需重启应用生效。
     */
    private String qqApi;

    /**
     * qq-api 上游接口调用超时时间（毫秒）。
     * 默认值：{@code 3000}。
     */
    private Integer qqApiTimeout = 3000;

    /**
     * 随机头像服务地址模板（{@code %s} 为种子，QQ 号场景即 QQ 号）。
     * <p>
     * qq-api 未返回头像时的降级拼接地址；留空或未配置时 /qq-info 响应数据全为空。
     */
    private String avatarApi;

}
