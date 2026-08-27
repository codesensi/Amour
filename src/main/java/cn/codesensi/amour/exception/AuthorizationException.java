package cn.codesensi.amour.exception;

import cn.codesensi.amour.core.ResultCode;

/**
 * 授权异常 —— 表示用户权限不足。
 *
 * @author codesensi
 * @since 1.0
 */
public class AuthorizationException extends BusinessException {

    /**
     * 构造授权异常，默认错误码为 {@link ResultCode#FORBIDDEN}（403，无权限访问该资源）。
     *
     * @param msg 错误描述信息
     */
    public AuthorizationException(String msg) {
        super(ResultCode.FORBIDDEN.getCode(), msg);
    }
}
