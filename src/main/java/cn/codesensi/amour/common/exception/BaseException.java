package cn.codesensi.amour.common.exception;

import lombok.Getter;

/**
 * 业务异常基类。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
public class BaseException extends RuntimeException {

    /**
     * 业务错误码，用于前端识别错误类型并进行差异化处理。
     * <p>
     * 推荐按模块分段编排，例如：
     * <ul>
     *   <li>1xxx — 通用错误（参数校验、鉴权等）；</li>
     *   <li>2xxx — 用户模块；</li>
     *   <li>3xxx — 资源模块；</li>
     *   <li>4xxx — 系统模块。</li>
     * </ul>
     */
    private final Integer code;

    /**
     * 错误描述信息，向前端展示的人类可读错误提示。
     */
    private final String msg;

    /**
     * 构造不含原始异常的业务异常。
     *
     * @param code 业务错误码
     * @param msg  错误描述信息
     */
    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造带原始异常的业务异常，保留底层异常链以便排查问题。
     *
     * @param code  业务错误码
     * @param msg   错误描述信息
     * @param cause 原始异常（通常为 catch 到的异常）
     */
    public BaseException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

}
