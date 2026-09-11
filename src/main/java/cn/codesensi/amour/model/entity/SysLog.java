package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统日志实体。
 * <p>
 * 对应 {@code sys_log} 表，存储标注了 {@code @Log} 注解的接口操作日志：
 * 登录人、请求地址、IP 归属地、请求参数（脱敏）、响应结果、耗时与成败状态等。
 *
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_log")
public class SysLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 日志类型: 0-未知, 1-登录, 2-登出, 3-查询, 4-新增, 5-修改, 6-删除, 7-授权, 8-上传, 9-下载
     */
    private Integer logType;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 请求类方法
     */
    private String method;

    /**
     * 请求接口地址
     */
    private String url;

    /**
     * 请求参数(JSON,脱敏后截断存储)
     */
    private String param;

    /**
     * 响应结果(JSON,截断存储)
     */
    private String result;

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

}
