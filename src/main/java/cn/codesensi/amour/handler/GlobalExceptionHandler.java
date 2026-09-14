package cn.codesensi.amour.handler;

import cn.codesensi.amour.common.core.Result;
import cn.codesensi.amour.common.core.ResultCode;
import cn.codesensi.amour.common.exception.AuthorizationException;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.exception.ValidationException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.ObjUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 * <p>
 * 将各类异常统一转换为 {@link Result} 响应，避免异常堆栈直接暴露给前端；
 * 处理顺序为「具体异常优先，兜底 Exception 收尾」。
 * <p>
 * 错误语义同时通过两层表达：HTTP 传输层状态码按异常类别映射真实语义
 * （4xx/5xx，见各 handler 与 {@link #toHttpStatus(int)}），便于网关、监控与通用
 * HTTP 客户端感知；响应体内 {@link Result#getCode()} 仍保留业务码（借用 HTTP 语义），
 * 响应体结构与既有契约保持一致，前端按统一契约解析即可。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理授权异常，返回无权限错误（业务码 403）。
     *
     * @param e 授权异常
     * @return 无权限（403 语义）统一响应
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Result<Void>> handleAuthorizationException(AuthorizationException e) {
        log.warn("AuthorizationException 授权异常：{}", e.getMsg());
        return wrap(HttpStatus.FORBIDDEN, Result.forbidden(e.getMsg()));
    }

    /**
     * 处理参数校验异常，返回请求参数错误（业务码 400）。
     *
     * @param e 参数校验异常
     * @return 请求参数错误（400 语义）统一响应
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result<Void>> handleValidationException(ValidationException e) {
        log.warn("ValidationException 参数异常：{}", e.getMsg());
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest(e.getMsg()));
    }

    /**
     * 处理业务异常，透传其业务错误码与描述；
     * HTTP 状态码按业务码语义映射（见 {@link #toHttpStatus(int)}）。
     *
     * @param e 业务异常
     * @return 携带业务错误码的统一响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException 业务异常：{}", e.getMsg());
        HttpStatus status = toHttpStatus(e.getCode());
        return wrap(status, Result.error(e.getCode(), e.getMsg()));
    }

    /**
     * 处理系统异常，透传其错误码与描述；HTTP 状态码统一以 500 表达。
     *
     * @param e 系统异常
     * @return 携带错误码的统一响应
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Result<Void>> handleSystemException(SystemException e) {
        log.error("SystemException 系统异常：", e);
        return wrap(HttpStatus.INTERNAL_SERVER_ERROR, Result.error(e.getCode(), e.getMsg()));
    }

    /**
     * 处理 Sa-Token 授权异常，按异常类型细分响应：
     * 未登录返回 401，账号被冻结返回 403，其余透传原始描述。
     *
     * @param e Sa-Token 授权异常
     * @return 携带错误码的统一响应
     */
    @ExceptionHandler(SaTokenException.class)
    public ResponseEntity<Result<Void>> handleSaTokenException(SaTokenException e) {
        log.warn("SaTokenException 授权异常：{}", e.getMessage());
        if (e instanceof NotLoginException notLoginException) {
            if (NotLoginException.TOKEN_FREEZE.equals(notLoginException.getType())) {
                return wrap(HttpStatus.FORBIDDEN, Result.forbidden("账号已被冻结"));
            }
            return wrap(HttpStatus.UNAUTHORIZED, Result.unauthorized("未登录或登录已过期"));
        }
        return wrap(HttpStatus.FORBIDDEN, Result.forbidden(e.getMessage()));
    }

    /**
     * 处理表单绑定/参数校验异常，优先取首个字段错误的提示信息。
     *
     * @param e 表单绑定异常
     * @return 请求参数错误（400）统一响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        log.warn("BindException 参数异常：{}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "参数校验未通过";
        if (ObjUtil.isNotNull(fieldError)) {
            message = fieldError.getDefaultMessage();
        }
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest(message));
    }

    /**
     * 处理请求体不可读异常（JSON 格式错误等），返回请求参数错误。
     *
     * @param e 请求体不可读异常
     * @return 请求参数错误（400 语义）统一响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException 请求体解析异常：{}", e.getMessage());
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest("请求体格式错误"));
    }

    /**
     * 处理请求参数类型不匹配异常（路径/查询参数无法转换为目标类型），返回请求参数错误。
     *
     * @param e 参数类型不匹配异常
     * @return 请求参数错误（400 语义）统一响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException 参数类型异常：{}", e.getMessage());
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest("请求参数类型不正确"));
    }

    /**
     * 处理缺少必填请求参数异常，返回请求参数错误。
     *
     * @param e 缺少请求参数异常
     * @return 请求参数错误（400 语义）统一响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException 缺少参数：{}", e.getParameterName());
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest("缺少必要的请求参数：" + e.getParameterName()));
    }

    /**
     * 处理请求方法不支持异常，返回方法不支持错误（业务码 405）。
     *
     * @param e 请求方法不支持异常
     * @return 方法不支持（405 语义）统一响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException 请求方法异常：{}", e.getMessage());
        return wrap(HttpStatus.METHOD_NOT_ALLOWED, Result.error(ResultCode.METHOD_NOT_ALLOWED.getCode(), "请求方法不支持"));
    }

    /**
     * 处理上传文件超出大小限制异常，返回请求参数错误。
     *
     * @param e 上传大小超限异常
     * @return 请求参数错误（400 语义）统一响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("MaxUploadSizeExceededException 上传大小超限：{}", e.getMessage());
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest("上传文件大小超出限制"));
    }

    /**
     * 处理约束校验异常（@Validated 方法级参数校验触发），取首个违规提示。
     *
     * @param e 约束校验异常
     * @return 请求参数错误（400 语义）统一响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("ConstraintViolationException 参数异常：{}", e.getMessage());
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数校验未通过");
        return wrap(HttpStatus.BAD_REQUEST, Result.badRequest(message));
    }

    /**
     * 处理静态资源未找到异常（如 404 的静态路径）。
     *
     * @param e 资源未找到异常
     * @return 资源不存在（404）统一响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        String path = e.getResourcePath();
        log.warn("NoResourceFoundException 资源异常：path={}", path);
        return wrap(HttpStatus.NOT_FOUND, Result.notFound("[" + path + "]不存在"));
    }

    /**
     * 兜底处理其他未捕获异常，返回系统内部错误响应。
     * <p>
     * 原始异常信息仅记录日志，不透传前端，避免泄露 SQL 片段、类名、路径等内部细节。
     *
     * @param e 未捕获的异常
     * @return 系统内部错误（500 语义）统一响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("Exception 未处理异常：", e);
        return wrap(HttpStatus.INTERNAL_SERVER_ERROR, Result.systemError("系统繁忙，请稍后重试"));
    }

    /**
     * 将业务错误码映射为 HTTP 传输层状态码。
     * <p>
     * 映射规则：code 落在 4xx 区间时直接采用（语义与业务码一致）；
     * 5xx 区间统一以 500 表达（含借用 504 语义的数据库异常，避免被网关按超时干预）；
     * 其余非 HTTP 语义的业务码按请求参数错误（400）处理。
     *
     * @param code 业务错误码
     * @return 对应的 HTTP 状态码
     */
    private HttpStatus toHttpStatus(int code) {
        if (code >= 500 && code != ResultCode.SERVICE_UNAVAILABLE.getCode()) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        HttpStatus status = HttpStatus.resolve(code);
        return ObjUtil.isNotNull(status) && status.isError() ? status : HttpStatus.BAD_REQUEST;
    }

    /**
     * 组装带传输层状态码的统一响应。
     *
     * @param status HTTP 状态码
     * @param body   统一响应体
     * @return 携带状态码的响应实体
     */
    private ResponseEntity<Result<Void>> wrap(HttpStatus status, Result<Void> body) {
        return ResponseEntity.status(status).body(body);
    }
}
