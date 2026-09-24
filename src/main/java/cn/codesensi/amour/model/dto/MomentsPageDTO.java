package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 点点滴滴分页查询参数 DTO（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MomentsPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章标题（模糊匹配）
     */
    private String title;

    /**
     * 文章分类（精确匹配）
     */
    private String category;

    /**
     * 状态（精确匹配）
     */
    private Integer status;

}
