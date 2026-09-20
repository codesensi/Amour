package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 足迹地图条目行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * 字段语义与 {@code portal_footprint} 表列一一对应；
 * 照片地址为入库的 photo_url 原值（站内 /file/view/{id} 或外链），
 * 门户与管理端响应共用该形态。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FootprintDTO implements Serializable {

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
     * 精确地点名称（可空）
     */
    private String placeName;

    /**
     * 经度（可空;地图展示用）
     */
    private BigDecimal longitude;

    /**
     * 纬度（可空）
     */
    private BigDecimal latitude;

    /**
     * 到访日期（yyyy-MM-dd）
     */
    private String arrivalDate;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    private Integer hidden;

    /**
     * 关联照片地址（站内 /file/view/{id} 或外链;无照片为 null）
     */
    private String photoUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
