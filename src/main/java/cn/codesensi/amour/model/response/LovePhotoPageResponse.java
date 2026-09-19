package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 恋爱相册照片分页查询行数据响应结果（管理端，完整字段）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 照片地址
     */
    private String url;

    /**
     * 照片文案
     */
    private String caption;

    /**
     * 照片日期（格式:yyyy-MM-dd）
     */
    private String dateText;

    /**
     * 照片标签（多值以逗号分隔;null 表示无标签）
     */
    private String tags;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
