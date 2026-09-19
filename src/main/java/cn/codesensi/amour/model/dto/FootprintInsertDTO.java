package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增足迹请求参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FootprintInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 城市/地点名称
     */
    private String city;

    /**
     * 精确地点名称(地图选点搜索选中的地点,或手动录入;可空)
     */
    private String placeName;

    /**
     * 经度(可空)
     */
    private BigDecimal longitude;

    /**
     * 纬度(可空)
     */
    private BigDecimal latitude;

    /**
     * 到访日期(可空)
     */
    private LocalDate arrivalDate;

    /**
     * 照片地址(可空)
     */
    private String photoUrl;

    /**
     * 备注
     */
    private String remark;

}
