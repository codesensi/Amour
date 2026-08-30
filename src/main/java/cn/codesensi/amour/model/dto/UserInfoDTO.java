package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 获取当前用户信息响应结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
public class UserInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 用户性别:U-未知,M-男,F-女
     */
    private String gender;

    /**
     * 备注
     */
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    private Integer sysFlag;

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
    private List<MenuDTO> menus;
}
