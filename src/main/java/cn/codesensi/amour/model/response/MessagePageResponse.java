package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言管理分页行响应 —— 管理端完整字段（含 IP 与审核状态）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MessagePageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 留言ID（序列化为字符串避免前端精度丢失）
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 访客昵称
     */
    private String nickname;

    /**
     * 留言头像快照
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言IP
     */
    private String ip;

    /**
     * IP归属地
     */
    private String region;

    /**
     * 审核状态: pending-待审核， approved-通过， rejected-驳回
     */
    private String auditStatus;

    /**
     * 留言时间（yyyy-MM-dd HH:mm:ss）
     */
    private String createTime;

}
