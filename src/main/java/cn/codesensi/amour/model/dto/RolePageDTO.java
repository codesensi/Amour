package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色分页查询参数
 *
 * @author codesensi
 * @since 2026-09-06
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RolePageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称(模糊匹配)
     */
    private String name;

    /**
     * 角色编码(模糊匹配)
     */
    private String code;

    /**
     * 角色状态:0-启用,1-禁用
     */
    private Integer status;

}
