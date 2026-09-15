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
 * 通知已读记录实体。
 * <p>
 * 对应 {@code sys_notice_read} 表，维护用户与通知的已读绑定关系；
 * (user_id, notice_id) 全生命周期唯一，标记已读按唯一键幂等写入。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_notice_read")
public class SysNoticeRead extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 用户ID（sys_user.id）
     */
    private Long userId;

    /**
     * 通知ID（sys_notice.id）
     */
    private Long noticeId;

}
