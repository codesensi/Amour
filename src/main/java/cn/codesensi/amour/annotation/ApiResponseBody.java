package cn.codesensi.amour.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * 统一 API 响应体包装注解。
 * <p>
 * 标注在 Controller 类或方法上后，其返回值会被 {@code ApiResponseBodyAdvice}
 * 自动包装为 {@code Result} 统一响应对象；返回值已是 {@code Result} 类型时不重复包装。
 *
 * @author codesensi
 * @see cn.codesensi.amour.advice.ApiResponseBodyAdvice
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ResponseBody
public @interface ApiResponseBody {
}
