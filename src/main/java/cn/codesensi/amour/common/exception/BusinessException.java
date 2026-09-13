package cn.codesensi.amour.common.exception;


import cn.codesensi.amour.common.core.ResultCode;

/**
 * 业务异常 —— 表示业务逻辑处理过程中可预见的错误。
 *
 * @author codesensi
 * @since 1.0
 */
public class BusinessException extends BaseException {

    /**
     * 构造业务异常，默认错误码为 {@link ResultCode#BAD_REQUEST}（400）。
     * <p>
     * 业务失败属于请求方可感知、可自行纠正的错误，HTTP 语义上应为 4xx 而非 5xx；
     * 需表达系统级故障时请使用 {@link SystemException} 或显式指定错误码。
     *
     * @param msg 错误描述信息
     */
    public BusinessException(String msg) {
        super(ResultCode.BAD_REQUEST.getCode(), msg);
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
