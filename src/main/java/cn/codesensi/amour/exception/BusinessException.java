package cn.codesensi.amour.exception;


import cn.codesensi.amour.core.ResultCode;

/**
 * 业务异常 —— 表示业务逻辑处理过程中可预见的错误。
 *
 * @author codesensi
 * @since 1.0
 */
public class BusinessException extends BaseException {

    public BusinessException(String msg) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg);
    }

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
