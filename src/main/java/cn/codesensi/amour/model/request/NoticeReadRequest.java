package cn.codesensi.amour.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 标记通知已读请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class NoticeReadRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID列表（雪花ID字符串化传输；空或缺失表示标记全部未读）
     */
    private List<Long> noticeIds;
}
