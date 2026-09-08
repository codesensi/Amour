package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改菜单请求参数
 *
 * @author codesensi
 * @since 2026-09-06
 */
@Data
public class MenuUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父级菜单ID(0 表示根节点)
     */
    private Long pid;

    /**
     * 菜单名称
     */
    private String title;

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
     * 备注
     */
    private String remark;

}
