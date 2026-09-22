package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 门户留言响应 —— 字段对齐门户前端 {@code MessageItem} 契约
 * （date/location 为重命名映射，date 格式 yyyy-MM-dd HH:mm:ss）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalMessageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访客昵称
     */
    private String nickname;

    /**
     * 留言头像快照（空时由前端本地兜底图兜底）
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言时间（yyyy-MM-dd HH:mm:ss）
     */
    private String date;

    /**
     * IP归属地
     */
    private String location;

}
