package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改菜单状态请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MenuChangeStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单状态:0-启用，1-禁用
     */
    private Integer status;

}