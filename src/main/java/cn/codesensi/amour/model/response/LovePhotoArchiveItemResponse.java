package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 恋爱画册年份归档条目响应结果。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoArchiveItemResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片年份
     */
    private Integer year;

    /**
     * 该年份的照片数量
     */
    private Long count;

}
