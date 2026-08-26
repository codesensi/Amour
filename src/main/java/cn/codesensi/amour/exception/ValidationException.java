package cn.codesensi.amour.exception;


import cn.codesensi.amour.core.ResultCode;

/**
 * 参数校验异常 —— 表示请求参数不符合校验规则。
 *
 * @author codesensi
 * @since 1.0
 */
public class ValidationException extends BusinessException {

    public ValidationException(String msg) {
        super(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    public ValidationException(int code, String msg) {
        super(code, msg);
    }
}
