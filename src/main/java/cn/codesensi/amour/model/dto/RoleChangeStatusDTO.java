package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改角色状态业务数据
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class RoleChangeStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色状态:0-启用，1-禁用
     */
    private Integer status;

}