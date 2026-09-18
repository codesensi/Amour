package cn.codesensi.amour.config;

import cn.codesensi.amour.common.jackson.TrimStringDeserializer;
import cn.hutool.core.date.DatePattern;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局配置。
 * <p>
 * 一、String 反序列化统一剥除首尾空白并置空为 null：覆盖所有 {@code @RequestBody}
 * 接口；{@code @Valid} 校验在反序列化之后执行，空格无法绕过必填校验。
 * 二、LocalDateTime 统一为 {@code yyyy-MM-dd HH:mm:ss}（NORM_DATETIME_PATTERN），
 * 全部时间字段免注解声明，时间格式的增改只维护此一处。
 * URL 查询参数与表单参数的 trim 由 {@code WebMvcConfig#addFormatters} 承接。
 *
 * @author codesensi
 * @since 1.0
 */
@Configuration
public class JacksonConfig {

    /**
     * String 反序列化统一清洗：剥除首尾空白，空串/纯空格置为 null。
     * <p>
     * 覆盖所有 {@code @RequestBody} 接口；{@code @Valid} 校验在反序列化之后执行，
     * 空格无法绕过必填校验；密码、富文本等按约定同样不保留首尾空格，无需豁免。
     */
    @Bean
    public JsonMapperBuilderCustomizer trimStringCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("amour-string");
            module.addDeserializer(String.class, new TrimStringDeserializer());
            builder.addModule(module);
        };
    }

    /**
     * LocalDateTime 统一格式为 {@code yyyy-MM-dd HH:mm:ss}（NORM_DATETIME_PATTERN）：
     * 序列化输出与反序列化解析共用同一格式，全部时间字段免注解声明，
     * 时间格式的增改只维护此一处。
     */
    @Bean
    public JsonMapperBuilderCustomizer localDateTimeFormatCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("amour-datetime");
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            builder.addModule(module);
        };
    }
}
