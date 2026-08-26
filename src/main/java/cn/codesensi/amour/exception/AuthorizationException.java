package cn.codesensi.amour.exception;

import cn.codesensi.amour.core.ResultCode;

/**
 * 授权异常 —— 表示用户权限不足。
 *
 * @author codesensi
 * @since 1.0
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException(String msg) {
        super(ResultCode.FORBIDDEN.getCode(), msg);
    }
}
