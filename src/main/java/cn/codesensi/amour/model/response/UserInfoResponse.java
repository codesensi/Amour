package cn.codesensi.amour.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 获取当前用户信息响应结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserInfoResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)  // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户身份证号码
     */
    private String idCard;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 用户QQ号码
     */
    private String qq;

    /**
     * 用户性别:U-未知,M-男,F-女
     */
    private String gender;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否内置:0-否,1-是
     */
    private Integer builtin;

    /**
     * 角色
     */
    private List<String> roles;

    /**
     * 权限
     */
    private List<String> perms;

    /**
     * 菜单
     */
    private List<MenuResponse> menus;
}
