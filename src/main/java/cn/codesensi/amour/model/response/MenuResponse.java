package cn.codesensi.amour.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单响应结果
 *
 * @author codesensi
 * @since 2026-07-15
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MenuResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 路由菜单ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 父级路由菜单ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
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
     * 菜单类型:1-目录,2-菜单,3-按钮
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
     * 系统内置标识:0-非内置,1-内置
     */
    private Integer sysFlag;
}
