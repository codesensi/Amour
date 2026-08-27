package cn.codesensi.amour;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Amour 应用启动类。
 * <p>
 * 组合开启以下能力：
 * <ul>
 *   <li>{@link SpringBootApplication} — Spring Boot 启动入口（含组件扫描与自动配置）；</li>
 *   <li>{@link MapperScan} — 扫描 {@code cn.codesensi.amour.**.mapper} 包下的 MyBatis 映射器接口；</li>
 *   <li>{@link EnableCaching} — 启用基于注解的缓存抽象（配合 Caffeine 缓存使用）；</li>
 *   <li>{@link ConfigurationPropertiesScan} — 扫描并注册 {@code @ConfigurationProperties} 配置类。</li>
 * </ul>
 *
 * @author codesensi
 * @since 1.0
 */
@ConfigurationPropertiesScan
@EnableCaching
@MapperScan("cn.codesensi.amour.**.mapper")
@SpringBootApplication
public class AmourApplication {

    /**
     * 应用启动入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AmourApplication.class, args);
    }

}
