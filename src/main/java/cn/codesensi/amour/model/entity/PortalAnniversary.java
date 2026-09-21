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
import java.time.LocalDate;

/**
 * 门户纪念日实体。
 * <p>
 * 对应 {@code portal_anniversary} 表，承载门户「纪念日」页面的重要日期记录；
 * 门户侧仅消费未删除的记录，管理端全量维护，删除走逻辑删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_anniversary")
public class PortalAnniversary extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 纪念日名称
     */
    private String name;

    /**
     * 纪念日类型（字典 anniversary-type，与 AnniversaryTypeEnum 编码对齐）
     */
    private String type;

    /**
     * 纪念日日期（每年重复时仅月/日生效）
     */
    private LocalDate anniversaryDate;

    /**
     * 是否每年重复: true-是, false-否
     */
    private Boolean repeatYearly;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示, 1-隐藏（门户侧固定过滤为显示）
     */
    private Integer hidden;

}
