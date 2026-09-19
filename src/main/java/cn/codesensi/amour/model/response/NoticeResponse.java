package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知条目响应结果（当前用户视角）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class NoticeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 当前用户是否已读
     */
    private Boolean read;

}
