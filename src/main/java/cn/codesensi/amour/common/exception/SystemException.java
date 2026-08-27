package cn.codesensi.amour.common.exception;


import cn.codesensi.amour.common.core.ResultCode;

/**
 * 系统异常 —— 表示不可预料的系统级错误。
 *
 * @author codesensi
 * @since 1.0
 */
public class SystemException extends BaseException {

    /**
     * 构造系统异常，默认错误码为 {@link ResultCode#INTERNAL_SERVER_ERROR}（500）。
     *
     * @param msg 错误描述信息
     */
    public SystemException(String msg) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg);
    }

    /**
     * 构造系统异常，指定错误码。
     *
     * @param code 错误码
     * @param msg  错误描述信息
     */
    public SystemException(int code, String msg) {
        super(code, msg);
    }

    /**
     * 构造系统异常，指定错误码并携带原始异常，保留异常链便于排查。
     *
     * @param code  错误码
     * @param msg   错误描述信息
     * @param cause 原始异常
     */
    public SystemException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
