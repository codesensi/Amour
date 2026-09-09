package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改用户状态请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class UserChangeStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户状态:0-启用，1-禁用
     */
    private Integer status;

}