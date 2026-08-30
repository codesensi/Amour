package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 路由菜单
 */
@Data
public class MenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 路由菜单ID
     */
    private Long id;

    /**
     * 父级路由菜单ID
     */
    private Long pid;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 菜单类型:D-目录,M-菜单,B-按钮
     */
    private String type;

    /**
     * 菜单排序
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
