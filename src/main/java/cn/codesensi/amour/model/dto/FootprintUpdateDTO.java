package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 修改足迹请求参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FootprintUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

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
     * 关联照片地址(可空;修改时显式写入,支持清空照片)
     */
    private String photoUrl;

    /**
     * 备注
     */
    private String remark;

}
