package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 门户纪念日行响应结果（免登录分页行与首页卡片）。
 * <p>
 * 字段名对齐前端契约（AnniversaryItem），
 * type 为字典 anniversary-type 的编码（birthday/anniversary/festival）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalAnniversaryResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 纪念日名称
     */
    private String name;

    /**
     * 纪念日类型（字典 anniversary-type 编码）
     */
    private String type;

    /**
     * 纪念日日期（yyyy-MM-dd；每年重复时仅月/日生效）
     */
    private String anniversaryDate;

    /**
     * 是否每年重复: true-是, false-否
     */
    private Boolean repeatYearly;

}
