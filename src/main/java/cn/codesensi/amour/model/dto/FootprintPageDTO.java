package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.time.LocalDate;

/**
 * 足迹地图分页查询参数 DTO（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FootprintPageDTO extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 城市/地点名称（模糊匹配）
     */
    private String city;

    /**
     * 到访日期范围起点（含;yyyy-MM-dd）
     */
    private LocalDate arrivalDateBegin;

    /**
     * 到访日期范围终点（含;yyyy-MM-dd）
     */
    private LocalDate arrivalDateEnd;

}
