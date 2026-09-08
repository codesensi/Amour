package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色全量列表行数据响应结果
 * <p>
 * 用作分配角色等场景的选项数据源,仅保留选项所需字段。
 *
 * @author codesensi
 * @since 2026-09-06
 */
@Data
public class RoleResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 是否内置:0-否,1-是
     */
    private Integer builtin;

}
