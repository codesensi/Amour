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
 * 点点滴滴文章实体，对应表 {@code portal_moments}。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_moments")
public class PortalMoments extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者ID（新增时取当前登录人）
     */
    private Long userId;

    /**
     * 文章内容（富文本 HTML）
     */
    private String content;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 文章分类（自由文本）
     */
    private String category;

    /**
     * 文章标签（逗号分隔,自由文本）
     */
    private String tags;

    /**
     * 状态（0-显示,1-隐藏）
     */
    private Integer status;

}
