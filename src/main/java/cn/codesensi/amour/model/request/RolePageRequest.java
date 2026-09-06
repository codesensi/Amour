package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色分页查询请求参数
 *
 * @author codesensi
 * @since 2026-09-06
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RolePageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称(模糊匹配)
     */
    @Size(max = 20, message = "角色名称长度不能超过20")
    private String name;

    /**
     * 角色编码(模糊匹配)
     */
    @Size(max = 20, message = "角色编码长度不能超过20")
    private String code;

    /**
     * 角色状态:0-启用,1-禁用
     */
    private Integer status;

}
