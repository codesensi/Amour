package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色信息实体。
 * <p>
 * 对应 {@code sys_role} 表，存储后台角色定义及其启停状态。
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
public class SysRole extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Id
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
     * 角色排序
     */
    private Integer sort;

    /**
     * 角色状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 内置标识:0-非内置,1-内置
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

}
