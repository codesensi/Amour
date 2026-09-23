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
import java.time.LocalDateTime;

/**
 * 时间胶囊实体。
 * <p>
 * 对应 {@code portal_time_capsule} 表，承载门户「时间胶囊」的封存信件数据；
 * 核心语义为「写入时封存、到期解锁」——门户侧未到 {@code open_time} 的记录
 * 绝不下发信件内容（防抓包剧透），管理端全量维护，删除走逻辑删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_time_capsule")
public class PortalTimeCapsule extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 信件内容
     */
    private String content;

    /**
     * 解锁时间（到点后门户可见全文）
     */
    private LocalDateTime openTime;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
