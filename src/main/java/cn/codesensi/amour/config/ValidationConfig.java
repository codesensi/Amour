package cn.codesensi.amour.config;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 参数校验配置。
 * <p>
 * 开启快速失败（fail-fast）：校验遇到第一个不合法的约束立即返回，不再收集后续违规。
 * 校验失败仍由 {@code GlobalExceptionHandler} 的 handleBindException 统一转换为 400 响应。
 * <p>
 * 通过 {@link ValidationConfigurationCustomizer} 钩子定制自动装配的 LocalValidatorFactoryBean，
 * 而非整段替换 Validator Bean——保留 Spring MessageSource 消息解析、自定义约束校验器
 * 依赖注入等自动装配能力不受影响。
 *
 * @author codesensi
 * @since 1.0
 */
@Configuration
public class ValidationConfig {

    /**
     * 定制 Spring Boot 自动装配的 LocalValidatorFactoryBean：开启快速失败。
     * <p>
     * jakarta validation 的通用 {@code Configuration} 接口不含 failFast 方法，
     * 实际类型为 Hibernate Validator 的配置，故需向下转型后开启。
     *
     * @param configuration jakarta validation 配置
     * @return 校验配置定制器
     */
    @Bean
    public ValidationConfigurationCustomizer failFastValidatorCustomizer() {
        return configuration -> ((HibernateValidatorConfiguration) configuration).failFast(true);
    }
}
