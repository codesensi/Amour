package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 情侣日记实体，对应表 {@code portal_diary}。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_diary")
public class PortalDiary extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 记录人ID（双人日记按人分栏;新增时取当前登录人）
     */
    private Long userId;

    /**
     * 记录日期
     */
    private LocalDate diaryDate;

    /**
     * 心情标识（sunny/rainy/starry;空为未标记）
     */
    private String mood;

    /**
     * 日记内容
     */
    private String content;

}
