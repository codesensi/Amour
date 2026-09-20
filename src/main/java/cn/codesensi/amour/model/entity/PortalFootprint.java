package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 足迹地图实体。
 * <p>
 * 对应 {@code portal_footprint} 表，承载门户「足迹地图」的到访记录；
 * 门户侧仅消费未删除的记录，管理端全量维护，删除走逻辑删除。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_footprint")
public class PortalFootprint extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 城市/地点名称
     */
    private String city;

    /**
     * 精确地点名称（地图选点搜索选中的地点，或手动录入;可空）
     */
    private String placeName;

    /**
     * 经度（GCJ-02 坐标系，与高德地图一致;选点或手动录入）
     */
    private BigDecimal longitude;

    /**
     * 纬度（GCJ-02 坐标系，与高德地图一致）
     */
    private BigDecimal latitude;

    /**
     * 到访日期（yyyy-MM-dd）
     */
    private LocalDate arrivalDate;

    /**
     * 显隐标识: 0-显示, 1-隐藏（门户侧仅消费显示的记录）
     */
    private Integer hidden;

    /**
     * 照片地址（站内 /file/view/{id} 或外链;空表示无照片）
     */
    private String photoUrl;

    /**
     * 备注
     */
    private String remark;

}
