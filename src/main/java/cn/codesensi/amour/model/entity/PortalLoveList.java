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
 * 恋爱清单项实体。
 * <p>
 * 对应 {@code portal_love_list} 表，承载门户「恋爱清单」的愿望条目数据；
 * 门户侧仅消费显隐为「显示」的记录，管理端全量维护，删除走逻辑删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_love_list")
public class PortalLoveList extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 清单内容
     */
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    private Integer done;

    /**
     * 纪念照地址（完成项可选）
     */
    private String photo;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
