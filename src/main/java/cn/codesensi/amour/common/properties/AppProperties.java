package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目配置属性 —— 映射 {@code app.*} 配置项。
 * <p>
 * 通过 {@link ConfigurationProperties} 绑定 {@code application.yml} 中 {@code app} 前缀下的所有配置，
 * 提供项目基本信息（名称、版本、负责人、版权）以及功能开关（演示模式、请求缓存限制等）。
 * 注入方式：
 * <pre>{@code
 * @Autowired
 * private AppProperties appProperties;
 * // 或构造注入
 * private final AppProperties appProperties;
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 项目名称，用于页面标题、日志标识等场景。
     */
    private String name;

    /**
     * 项目版本号
     */
    private String version;

    /**
     * 项目负责人/维护者标识。
     */
    private String author;

    /**
     * 版权年份，用于页面底部版权声明。
     */
    private String copyright;

    /**
     * 演示模式开关。
     * <ul>
     *   <li>{@code true} — 开启演示模式，新增/修改/删除等写操作将被拦截</li>
     *   <li>{@code false} — 正常模式，所有操作不受限制</li>
     * </ul>
     */
    private boolean demoMode = false;

    /**
     * 用户随机头像服务地址
     */
    private String avatar;

}
