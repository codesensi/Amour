package cn.codesensi.amour.exception;


import cn.codesensi.amour.core.ResultCode;

/**
 * 业务异常 —— 表示业务逻辑处理过程中可预见的错误。
 *
 * @author codesensi
 * @since 1.0
 */
public class BusinessException extends BaseException {

    /**
     * 构造业务异常，默认错误码为 {@link ResultCode#INTERNAL_SERVER_ERROR}（500）。
     *
     * @param msg 错误描述信息
     */
    public BusinessException(String msg) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg);
    }

    /**
     * 构造业务异常，指定业务错误码。
     *
     * @param code 业务错误码
     * @param msg  错误描述信息
     */
    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    /**
     * 构造业务异常，指定错误码并携带原始异常，保留异常链便于排查。
     *
     * @param code  业务错误码
     * @param msg   错误描述信息
     * @param cause 原始异常
     */
    public BusinessException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
