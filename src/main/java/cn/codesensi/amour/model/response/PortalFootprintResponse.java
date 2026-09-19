package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门户足迹地图响应结果
 * <p>
 * 字段名对齐前端契约（FootprintItem），照片地址为免登录分发地址。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalFootprintResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 城市/地点名称
     */
    private String city;

    /**
     * 精确地点名称(可空)
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
     * 到访日期(yyyy-MM-dd)
     */
    private String arrivalDate;

    /**
     * 关联照片地址(无照片为 null)
     */
    private String photoUrl;

    /**
     * 备注
     */
    private String remark;

}
