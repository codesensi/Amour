package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 时间胶囊分页查询请求参数（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TimeCapsulePageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标题（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_128, message = "标题长度不能超过" + AppConst.MAX_LENGTH_128)
    private String title;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
