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
 * 菜单实体。
 * <p>
 * 对应 {@code sys_menu} 表，存储后台导航菜单、路由信息与按钮权限点，
 * 通过 {@code pid} 构建树形结构。
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_menu")
public class SysMenu extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @Id
    private Long id;

    /**
     * 父级菜单ID
     */
    private Long pid;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 菜单类型:D-目录,M-菜单,B-按钮
     */
    private String type;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单排序:数字越小越靠前
     */
    private Integer sort;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 权限编码
     */
    private String perms;

    /**
     * 菜单状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 显隐标识:0-显示,1-隐藏
     */
    private Integer hidden;

    /**
     * 内置标识:0-非内置,1-内置
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

}
