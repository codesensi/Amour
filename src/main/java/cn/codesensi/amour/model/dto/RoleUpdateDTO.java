package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改角色业务数据。
 * <p>
 * 字段校验在 Request 层（控制器 @Valid）完成，DTO 不重复标注。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class RoleUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

}
