package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改照片显隐业务数据。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LovePhotoChangeHiddenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 照片ID
     */
    private Long id;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    private Integer hidden;

}
