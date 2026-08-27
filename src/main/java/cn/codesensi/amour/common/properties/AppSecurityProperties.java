package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目安全配置属性 —— 映射 {@code app.security.*} 配置项。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    /**
     * 请求体缓存的最大字节数。
     * <p>
     * 限制 {@link org.springframework.web.util.ContentCachingRequestWrapper} 可缓存的最大请求体大小，
     * 避免超大请求体（如文件上传）导致内存溢出。
     * 默认值：{@code 1048576}（1MB）。
     */
    private Integer requestCacheLimit = 1048576;

}
