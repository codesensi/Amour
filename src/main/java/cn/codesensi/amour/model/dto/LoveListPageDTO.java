package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 恋爱清单分页查询参数 DTO（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoveListPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清单内容（模糊匹配）
     */
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    private Integer done;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
