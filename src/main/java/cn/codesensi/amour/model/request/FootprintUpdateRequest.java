package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RegexConst;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改足迹请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FootprintUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "足迹ID不能为空")
    private Long id;

    /**
     * 城市/地点名称
     */
    @NotBlank(message = "城市不能为空")
    @Size(max = AppConst.MAX_LENGTH_128, message = "城市名称长度不能超过" + AppConst.MAX_LENGTH_128)
    private String city;

    /**
     * 精确地点名称(地图选点搜索选中的地点,或手动录入;可空)
     */
    @Size(max = AppConst.MAX_LENGTH_128, message = "精确地点长度不能超过" + AppConst.MAX_LENGTH_128)
    private String placeName;

    /**
     * 经度(GCJ-02,由地图选点或手动录入;-180~180;可空)
     */
    @DecimalMin(value = "-180", message = "经度范围为-180~180")
    @DecimalMax(value = "180", message = "经度范围为-180~180")
    private BigDecimal longitude;

    /**
     * 纬度(GCJ-02;-90~90;可空)
     */
    @DecimalMin(value = "-90", message = "纬度范围为-90~90")
    @DecimalMax(value = "90", message = "纬度范围为-90~90")
    private BigDecimal latitude;

    /**
     * 到访日期(格式:yyyy-MM-dd)
     */
    @NotBlank(message = "到访日期不能为空")
    @Pattern(regexp = RegexConst.DIGITS_4_2_2, message = "到访日期格式须为yyyy-MM-dd")
    private String arrivalDate;

    /**
     * 关联照片地址(站内 /file/view/{id} 或外链;可空,显式传入支持清空照片)
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "照片地址长度不能超过" + AppConst.MAX_LENGTH_512)
    private String photoUrl;

    /**
     * 备注
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "备注长度不能超过" + AppConst.MAX_LENGTH_512)
    private String remark;

}
