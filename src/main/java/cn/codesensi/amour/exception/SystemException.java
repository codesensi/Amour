package cn.codesensi.amour.exception;


import cn.codesensi.amour.core.ResultCode;

/**
 * 系统异常 —— 表示不可预料的系统级错误。
 *
 * @author codesensi
 * @since 1.0
 */
public class SystemException extends BaseException {

    public SystemException(String msg) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg);
    }

    public SystemException(int code, String msg) {
        super(code, msg);
    }

    public SystemException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
