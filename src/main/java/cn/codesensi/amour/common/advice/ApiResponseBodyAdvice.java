package cn.codesensi.amour.common.advice;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.Result;
import cn.codesensi.amour.common.core.ResultCode;
import cn.codesensi.amour.common.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.lang.annotation.Annotation;

/**
 * Api 响应体通知处理器 —— 为标注了 {@link ApiResponseBody} 的 Controller 自动封装统一响应对象。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 触发统一响应包装的注解类型 —— {@link ApiResponseBody}。
     * <p>
     * 只有标注了该注解的类或方法，其返回值才会被本处理器拦截并包装。
     */
    private static final Class<? extends Annotation> ANNOTATION_TYPE = ApiResponseBody.class;

    /**
     * Jackson 序列化器（Spring MVC 注入，与全局 JSON 序列化配置保持一致）。
     */
    private final ObjectMapper objectMapper;

    /**
     * 判断当前请求的 Controller 类或方法是否标注了 {@link ApiResponseBody} 注解。
     * <p>
     * 匹配规则：
     * <ul>
     *   <li>类级别：使用 {@link AnnotatedElementUtils#hasAnnotation} 查找，支持派生注解（如 {@code @AliasFor} 组合）的匹配；</li>
     *   <li>方法级别：使用 {@link MethodParameter#hasMethodAnnotation} 直接判断方法上是否存在该注解。</li>
     * </ul>
     * 类或方法任一匹配即返回 {@code true}，触发响应体包装。
     *
     * @param methodParameter 当前方法参数元信息，包含所属类和方法上的注解信息
     * @param clazz           当前使用的 HttpMessageConverter 类型
     * @return true 表示需要执行 {@link #beforeBodyWrite} 对响应体进行包装
     */
    @Override
    public boolean supports(MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> clazz) {
        return AnnotatedElementUtils.hasAnnotation(methodParameter.getContainingClass(), ANNOTATION_TYPE)
                || methodParameter.hasMethodAnnotation(ANNOTATION_TYPE);
    }

    /**
     * 将 Controller 返回的原始数据包装为统一响应对象 {@link Result}。
     * <p>
     * 处理逻辑按以下优先级（短路匹配）：
     * <ol>
     *   <li><b>String 类型特殊处理</b> — Spring MVC 的 {@code StringHttpMessageConverter}
     *       会直接将字符串写入响应流，不再经过全局 JSON 序列化。
     *       因此需要手动用 Jackson 将包装结果转为 JSON 字符串，并将 Content-Type
     *       置为 application/json（避免默认落为 text/html），确保统一响应格式一致；</li>
     *   <li><b>Result 类型直接放行</b> — 如果返回结果已经是 {@link Result} 实例
     *       （例如 Feign 调用或已手动包装的结果），则直接返回，避免重复包装；</li>
     *   <li><b>默认包装</b> — 其他类型统一使用 {@code Result.success(body)} 包装为成功响应。</li>
     * </ol>
     *
     * @param body            Controller 方法返回的原始数据对象
     * @param methodParameter 当前方法参数元信息
     * @param mediaType       响应需要使用的内容类型（当前实现未使用，保留以符合接口契约）
     * @param clazz           当前使用的 HttpMessageConverter 类型
     * @param request         当前服务端 HTTP 请求
     * @param response        当前服务端 HTTP 响应
     * @return 包装后的统一响应对象（已经是序列化就绪状态）
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter methodParameter,
                                  @NonNull MediaType mediaType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> clazz,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        // 处理 String 类型 —— StringHttpMessageConverter 绕过 JSON 序列化，
        // 必须手动用 Jackson 将 Result 序列化为 JSON 字符串写入响应流（与全局序列化配置一致），
        // 并将 Content-Type 置为 application/json，避免 StringHttpMessageConverter 默认的 text/html
        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (JacksonException e) {
                throw new SystemException(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "响应体序列化为 JSON 失败", e);
            }
        }
        // 如果已经被包装过（Result 类型），则直接放行，避免双重包装
        if (body instanceof Result) {
            return body;
        }
        // 默认情况：将任意业务数据包装为统一成功响应
        return Result.success(body);
    }

}
