package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 恋爱画册年份归档条目 DTO。
 * <p>
 * 承载按年份聚合的照片数量，用于门户画册的年份导航。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoArchiveItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片年份（取自照片日期前四位）
     */
    private Integer year;

    /**
     * 该年份的照片数量
     */
    private Long count;

}
