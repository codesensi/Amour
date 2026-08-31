package cn.codesensi.amour.handler;

import cn.codesensi.amour.common.core.Result;
import cn.codesensi.amour.common.exception.AuthorizationException;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.exception.ValidationException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 * <p>
 * 将各类异常统一转换为 {@link Result} 响应，避免异常堆栈直接暴露给前端；
 * 处理顺序为「具体异常优先，兜底 Exception 收尾」。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理授权异常，返回 403 无权限响应。
     *
     * @param e 授权异常
     * @return 无权限（403）统一响应
     */
    @ExceptionHandler(AuthorizationException.class)
    public Result<Void> handleAuthorizationException(AuthorizationException e) {
        log.error("授权异常：", e);
        return Result.forbidden(e.getMsg());
    }

    /**
     * 处理参数校验异常，返回 400 响应。
     *
     * @param e 参数校验异常
     * @return 请求参数错误（400）统一响应
     */
    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidationException(ValidationException e) {
        log.error("参数异常：", e);
        return Result.badRequest(e.getMsg());
    }

    /**
     * 处理业务异常，透传其业务错误码与描述。
     *
     * @param e 业务异常
     * @return 携带业务错误码的统一响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理系统异常，透传其错误码与描述。
     *
     * @param e 系统异常
     * @return 携带错误码的统一响应
     */
    @ExceptionHandler(SystemException.class)
    public Result<Void> handleSystemException(SystemException e) {
        log.error("系统异常：", e);
        return Result.error(e.getCode(), e.getMsg());
    }

    // Sa-Token 异常细分处理
    @ExceptionHandler(SaTokenException.class)
    public Result<Void> handleSaTokenException(SaTokenException e) {
        log.error("授权异常：", e);
        if (e instanceof NotLoginException notLoginException) {
            if (NotLoginException.TOKEN_FREEZE.equals(notLoginException.getType())) {
                return Result.forbidden("账号已被冻结");
            }
            return Result.unauthorized("未登录或登录已过期");
        }
        return Result.forbidden(e.getMessage());
    }

    /**
     * 处理表单绑定/参数校验异常，优先取首个字段错误的提示信息。
     *
     * @param e 表单绑定异常
     * @return 请求参数错误（400）统一响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        log.error("参数异常：", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "参数校验未通过";
        if (ObjUtil.isNotNull(fieldError)) {
            message = fieldError.getDefaultMessage();
        }
        return Result.badRequest(message);
    }

    /**
     * 处理静态资源未找到异常（如 404 的静态路径）。
     *
     * @param e 资源未找到异常
     * @return 资源不存在（404）统一响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        String path = e.getResourcePath();
        log.warn("资源异常：", e);
        return Result.notFound("[" + path + "]不存在");
    }

    /**
     * 兜底处理其他未捕获异常，返回系统内部错误响应。
     *
     * @param e 未捕获的异常
     * @return 系统内部错误（500）统一响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未处理的异常：", e);
        return Result.systemError(e.getMessage());
    }
}
