package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存角色请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
public class RoleSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

}
