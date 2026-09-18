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
 * 恋爱相册照片实体。
 * <p>
 * 对应 {@code portal_love_photo} 表，承载门户「恋爱画册」的照片数据；
 * 门户侧仅消费显隐为「显示」的记录，管理端全量维护，删除走逻辑删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_love_photo")
public class PortalLovePhoto extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 照片地址
     */
    private String url;

    /**
     * 照片文案
     */
    private String caption;

    /**
     * 照片日期（格式：yyyy-MM-dd）
     */
    private String dateText;

    /**
     * 照片标签（旅行/日常/节日等，多值以逗号分隔存储）
     */
    private String tags;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    private Integer hidden;

}
