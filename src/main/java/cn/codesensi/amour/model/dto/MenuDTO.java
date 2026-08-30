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
     * 路由名称(外链地址)
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 路由参数
     */
    private String param;

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
     * 是否外链:0-否,1-是
     */
    private Integer isLink;

    /**
     * 是否内嵌iframe:0-否,1-是
     */
    private Integer isFrame;

    /**
     * 内嵌iframe地址
     */
    private String frameSrc;

    /**
     * 是否显示:0-否,1-是
     */
    private Integer isShow;

    /**
     * 是否显示父级菜单:0-否,1-是
     */
    private Integer isShowParent;

    /**
     * 菜单状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    private Integer sysFlag;

}
