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
