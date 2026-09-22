package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 留言条目 DTO —— Service 出参，实体仅在层内流转（对齐恋爱画册分层惯例）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 留言ID
     */
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
     * 留言时间
     */
    private LocalDateTime createTime;

}
