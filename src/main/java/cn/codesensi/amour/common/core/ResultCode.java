package cn.codesensi.amour.common.core;

import lombok.Getter;

/**
 * 统一响应状态码（基于 HTTP 语义）
 * 优点：前端无需维护额外的映射表，code 值直观反映接口状态
 */
@Getter
public enum ResultCode {

    // ---------- 2xx 成功 ----------
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 创建成功
     */
    CREATED(201, "创建成功"),
    /**
     * 操作成功，无返回内容
     */
    NO_CONTENT(204, "操作成功，无返回内容"),

    // ---------- 4xx 客户端错误 ----------
    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    /**
     * 未登录或登录已过期
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),
    /**
     * 无权限访问该资源
     */
    FORBIDDEN(403, "无权限访问该资源"),
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    /**
     * 请求方法不允许
     */
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    /**
     * 资源已存在
     */
    CONFLICT(409, "资源已存在"),
    /**
     * 资源已过期
     */
    GONE(410, "资源已过期"),
    /**
     * 请求过于频繁，请稍后重试
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),

    // ---------- 5xx 服务端错误 ----------
    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),
    /**
     * 服务暂不可用
     */
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    /**
     * 数据库异常（借用 HTTP 504，本项目内自定义语义）
     */
    DB_ERROR(504, "数据库异常"),
    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
