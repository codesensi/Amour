package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RegexConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 新增恋爱相册照片请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片地址
     */
    @NotBlank(message = "照片地址不能为空")
    @Size(max = AppConst.MAX_LENGTH_512, message = "照片地址长度不能超过" + AppConst.MAX_LENGTH_512)
    private String url;

    /**
     * 照片文案
     */
    @NotBlank(message = "照片文案不能为空")
    @Size(max = AppConst.MAX_LENGTH_512, message = "照片文案长度不能超过" + AppConst.MAX_LENGTH_512)
    private String caption;

    /**
     * 照片日期(格式:yyyy-MM-dd)
     */
    @NotBlank(message = "照片日期不能为空")
    @Pattern(regexp = RegexConst.DIGITS_4_2_2, message = "照片日期格式须为yyyy-MM-dd")
    private String dateText;

    /**
     * 照片标签集合(服务端规范化为逗号分隔存储,拼接后总长 ≤ 64)
     */
    private List<String> tags;

    /**
     * 排序(数字越小越靠前)
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    @NotNull(message = "显隐标识不能为空")
    private Integer hidden;

}
