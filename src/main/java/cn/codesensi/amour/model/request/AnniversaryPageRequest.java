package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * 纪念日分页查询请求参数（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AnniversaryPageRequest extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 纪念日名称（模糊匹配）
     */
    private String name;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
