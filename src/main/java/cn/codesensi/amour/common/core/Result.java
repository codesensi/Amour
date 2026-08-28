package cn.codesensi.amour.common.core;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * 统一接口响应对象
 *
 * @param <T> 响应数据的类型
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否成功；由 {@link ResultCode#SUCCESS} 的 code 值（200）判定。
     */
    private boolean success;

    /**
     * 业务状态码，语义同 {@link ResultCode}。
     */
    private int code;

    /**
     * 面向终端的可读提示信息。
     */
    private String msg;

    /**
     * 响应数据（可选，失败或无数据时为 null）。
     */
    private T data;

    /**
     * 响应生成时间戳（毫秒，Unix epoch）。
     */
    private long timestamp;

    /**
     * 私有构造器：仅允许通过下方静态工厂方法创建实例。
     *
     * @param code 业务状态码
     * @param msg  提示信息
     * @param data 响应数据
     */
    private Result(int code, String msg, T data) {
        this.success = code == ResultCode.SUCCESS.getCode();
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /**
     * 成功响应（自定义消息，带数据）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败响应（使用枚举）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /**
     * 失败响应（自定义消息，使用枚举）
     */
    public static <T> Result<T> error(ResultCode resultCode, String msg) {
        return new Result<>(resultCode.getCode(), msg, null);
    }

    /**
     * 失败响应（指定状态码和消息）
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败响应（使用 ResultCode，并附加额外数据）
     */
    public static <T> Result<T> error(ResultCode resultCode, T data) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), data);
    }

    /**
     * 快速失败：参数错误
     */
    public static <T> Result<T> badRequest(String msg) {
        return error(ResultCode.BAD_REQUEST, msg);
    }

    /**
     * 快速失败：未认证
     */
    public static <T> Result<T> unauthorized(String msg) {
        return error(ResultCode.UNAUTHORIZED, msg);
    }

    /**
     * 快速失败：无权限
     */
    public static <T> Result<T> forbidden(String msg) {
        return error(ResultCode.FORBIDDEN, msg);
    }

    /**
     * 快速失败：资源不存在
     */
    public static <T> Result<T> notFound(String msg) {
        return error(ResultCode.NOT_FOUND, msg);
    }

    /**
     * 快速失败：服务异常
     */
    public static <T> Result<T> systemError(String msg) {
        return error(ResultCode.INTERNAL_SERVER_ERROR, msg);
    }

}
