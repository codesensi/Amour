package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 新增照片业务数据。
 * <p>
 * 字段校验在 Request 层（控制器 @Valid）完成，DTO 不重复标注；
 * 标签集合在 Service 层规范化（trim/去空/去重）后以逗号分隔入库。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 照片标签集合（服务端规范化为逗号分隔存储，拼接后总长 ≤ 64）
     */
    private List<String> tags;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
