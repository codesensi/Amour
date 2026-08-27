package cn.codesensi.amour.exception;


import cn.codesensi.amour.core.ResultCode;

/**
 * 参数校验异常 —— 表示请求参数不符合校验规则。
 *
 * @author codesensi
 * @since 1.0
 */
public class ValidationException extends BusinessException {

    /**
     * 构造参数校验异常，默认错误码为 {@link ResultCode#BAD_REQUEST}（400，请求参数错误）。
     *
     * @param msg 错误描述信息
     */
    public ValidationException(String msg) {
        super(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    /**
     * 构造参数校验异常，指定错误码。
     *
     * @param code 错误码
     * @param msg  错误描述信息
     */
    public ValidationException(int code, String msg) {
        super(code, msg);
    }
}
