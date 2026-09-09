package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改菜单请求参数
 * <p>
 * 不接收 builtin/type 字段:二者均为结构性标识，创建后不允许通过修改接口变更。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MenuUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @NotNull(message = "菜单ID不能为空")
    private Long id;

    /**
     * 父级菜单ID(0 表示根节点)
     */
    @NotNull(message = "父级菜单ID不能为空")
    private Long pid;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 256, message = "菜单名称长度不能超过256")
    private String title;

    /**
     * 路由路径(目录/菜单必填，按钮无需填写)
     */
    @Size(max = 512, message = "路由路径长度不能超过512")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 256, message = "组件路径长度不能超过256")
    private String component;

    /**
     * 菜单排序:数字越小越靠前
     */
    private Integer sort;

    /**
     * 菜单图标
     */
    @Size(max = 256, message = "菜单图标长度不能超过256")
    private String icon;

    /**
     * 权限编码(按钮类型必填)
     */
    @Size(max = 64, message = "权限编码长度不能超过64")
    private String perms;

    /**
     * 菜单状态:0-启用，1-禁用
     */
    private Integer status;

    /**
     * 显隐标识:0-显示，1-隐藏
     */
    private Integer hidden;

    /**
     * 备注
     */
    @Size(max = 512, message = "备注长度不能超过512")
    private String remark;

}