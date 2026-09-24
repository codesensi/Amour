package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改点点滴滴文章状态业务数据。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsChangeStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 状态: 0-显示， 1-隐藏
     */
    private Integer status;

}
