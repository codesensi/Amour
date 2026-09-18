package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 恋爱相册照片分页查询参数 DTO（管理端）
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LovePhotoPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片文案(模糊匹配)
     */
    private String caption;

    /**
     * 照片标签(在逗号分隔集合中精确匹配)
     */
    private String tag;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    private Integer hidden;

}
