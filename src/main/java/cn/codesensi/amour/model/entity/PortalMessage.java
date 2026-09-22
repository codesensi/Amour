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
 * 留言实体。
 * <p>
 * 对应 {@code portal_message} 表，承载门户「留言簿」的访客留言数据；
 * 访客提交后为待审核状态，仅审核通过的记录在门户下发，管理端全量维护，删除走逻辑删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_message")
public class PortalMessage extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 访客昵称
     */
    private String nickname;

    /**
     * 留言头像
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

}
