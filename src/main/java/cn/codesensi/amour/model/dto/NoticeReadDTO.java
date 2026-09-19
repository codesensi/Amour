package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 标记通知已读业务数据。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class NoticeReadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID列表（空或缺失表示标记全部未读）
     */
    private List<Long> noticeIds;
}
