package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 恋爱相册照片行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * 字段语义与 {@code portal_love_photo} 表列一一对应；
 * 门户侧由 {@code PortalLovePhotoConverter} 重命名映射为前端契约（img/text/date），
 * 管理端侧同名直映为分页行响应。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
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
     * 照片日期（格式：yyyy-MM-dd）
     */
    private String dateText;

    /**
     * 照片标签（多值以逗号分隔存储；null 表示无标签）
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
