package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 足迹地图分页查询请求参数（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FootprintPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 城市/地点名称(模糊匹配)
     */
    @Size(max = AppConst.MAX_LENGTH_128, message = "城市名称长度不能超过" + AppConst.MAX_LENGTH_128)
    private String city;

    /**
     * 到访日期范围起点(含;yyyy-MM-dd)
     */
    @Size(max = 10, message = "到访日期格式须为yyyy-MM-dd")
    private String arrivalDateBegin;

    /**
     * 到访日期范围终点(含;yyyy-MM-dd)
     */
    @Size(max = 10, message = "到访日期格式须为yyyy-MM-dd")
    private String arrivalDateEnd;

}
