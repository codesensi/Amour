package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 项目应用配置属性 —— 映射 {@code app.*} 顶层配置项。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
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
     * UApiPro 获取 QQ 信息接口地址模板（{@code %s} 为 URL 编码后的 QQ 号）。
     * <p>
     * 留空或未配置时 /qq-info 不调用上游，直接降级 qq-avatar 拼接头像；
     * 配置文件改动需重启应用生效。
     */
    private String uapiQq;

    /**
     * QQ 官方头像接口地址模板（{@code %s} 为 QQ 号）。
     * <p>
     * uapi-qq 失败时的降级拼接地址；留空或未配置时 /qq-info 响应数据全为空。
     * 配置文件改动需重启应用生效。
     */
    private String qqAvatar;

    /**
     * UApiPro 接口调用超时时间（毫秒），适用于该上游全部接口。
     * 默认值：{@code 3000}。
     */
    private Integer uapiTimeout = 3000;

    /**
     * UApiPro 获取一言（随机）接口地址。
     */
    private String uapiSayingRandom;

    /**
     * UApiPro 获取一言（普通版）接口地址：响应仅含 text 字段，作为随机一言的降级来源。
     */
    private String uapiSaying;

}
