package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增角色请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class RoleInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 20, message = "角色名称长度不能超过20")
    private String name;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 20, message = "角色编码长度不能超过20")
    private String code;

    /**
     * 角色排序
     */
    private Integer sort;

    /**
     * 备注
     */
    @Size(max = 512, message = "备注长度不能超过512")
    private String remark;

}
