package cn.codesensi.amour.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志分页查询行数据响应结果。
 * <p>
 * 登录日志与操作日志共用本结构（同表存储、以日志类型区分）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LogPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作描述
     */
    private String operation;

    /**
     * 日志类型: 1-登录, 2-登出, 3-查询, 4-新增, 5-修改, 6-删除, 7-授权, 8-上传, 9-下载
     */
    private Integer logType;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * IP归属地
     */
    private String region;

    /**
     * 耗时（毫秒）
     */
    private Long elapsed;

    /**
     * 操作状态: 0-失败, 1-成功
     */
    private Integer status;

    /**
     * 描述/失败原因
     */
    private String msg;

    /**
     * 请求接口地址
     */
    private String url;

    /**
     * 请求参数(JSON,脱敏后截断存储)
     */
    private String param;

    /**
     * 响应结果(JSON,脱敏后截断存储)
     */
    private String result;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
