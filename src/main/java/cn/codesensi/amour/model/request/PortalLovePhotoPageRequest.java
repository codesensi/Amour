package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 恋爱画册照片分页查询请求参数（门户）。
 * <p>
 * 在门户分页参数基础上扩展年份与标签过滤（均可缺省，缺省时与原门户口径一致）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PortalLovePhotoPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片年份（按照片日期的前四位精确匹配；可空）
     */
    @Min(value = 1970, message = "照片年份不能早于1970")
    @Max(value = 2100, message = "照片年份不能晚于2100")
    private Integer year;

    /**
     * 照片标签（在逗号分隔集合中精确匹配；可空）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "照片标签长度不能超过" + AppConst.MAX_LENGTH_64)
    private String tag;

}
