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
 * 恋爱相册照片分页查询请求参数（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LovePhotoPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片文案(模糊匹配)
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "照片文案长度不能超过" + AppConst.MAX_LENGTH_512)
    private String caption;

    /**
     * 照片标签(在逗号分隔集合中精确匹配)
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "照片标签长度不能超过" + AppConst.MAX_LENGTH_64)
    private String tag;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    private Integer hidden;

}
