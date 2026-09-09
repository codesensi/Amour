package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改字典状态业务数据
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictChangeStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典条目ID
     */
    private Long id;

    /**
     * 字典状态:0-启用，1-禁用
     */
    private Integer status;

}