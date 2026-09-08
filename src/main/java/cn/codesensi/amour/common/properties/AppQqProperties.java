package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * QQ 信息查询配置属性 —— 映射 {@code app.qq.*} 配置项。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.qq")
public class AppQqProperties {

    /**
     * qq-service 上游接口调用超时时间（毫秒）。
     * 默认值：{@code 3000}。
     */
    private Integer timeout = 3000;

}
