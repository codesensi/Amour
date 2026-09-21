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
import java.time.LocalDate;

/**
 * 门户访问统计实体。
 * <p>
 * 对应 {@code portal_visit} 表，按日粒度记录门户的访问量（PV）与独立访客数（UV）；
 * 数据由访问上报自动生成，无业务审计语义（审计列继承自 {@link BaseEntity}）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("portal_visit")
public class PortalVisit extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 当日访问量（PV）
     */
    private Long pv;

    /**
     * 当日独立访客数（UV）
     */
    private Long uv;

}
