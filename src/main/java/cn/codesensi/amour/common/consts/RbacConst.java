package cn.codesensi.amour.common.consts;

/**
 * 权限常量 —— RBAC 相关的用户、角色、权限标识及公开/管理接口路径。
 *
 * @author codesensi
 * @since 1.0
 */
public class RbacConst {

    /**
     * 超级管理员用户ID
     */
    public static final Long USER_ADMIN_ID = AppConst.ONE_LONG;

    /**
     * 超级管理员名称
     */
    public static final String USER_ADMIN_NAME = "admin";

    /**
     * 超级管理员角色标识
     */
    public static final String ROLE_ADMIN_CODE = "admin";

    /**
     * 超级管理员权限码
     */
    public static final String PERM_ADMIN_CODE = "*:*:*";

    /**
     * 根接口路径
     */
    public static final String ROOT_PATH = "/**";

    /**
     * 验证码生成接口路径
     */
    public static final String CAPTCHA_PATH = "/captcha";

    /**
     * 登录接口路径
     */
    public static final String LOGIN_PATH = "/auth/login";

    /**
     * 退出登录接口路径
     */
    public static final String LOGOUT_PATH = "/auth/logout";

}
