package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改恋爱清单项显隐参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoveListChangeHiddenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清单项ID
     */
    private Long id;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
