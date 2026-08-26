package cn.codesensi.amour.consts;

/**
 * 权限常量
 */
public class RbacConst {

    /**
     * 超级管理员用户ID
     */
    public static final Long USER_ADMIN_ID = AppConst.ONE_LONG;

    /**
     * 超级管理员名称
     */
    public static final String USER_ADMIN_NAME = "sadmin";

    /**
     * 超级管理员角色标识
     */
    public static final String ROLE_ADMIN_CODE = "sadmin";

    /**
     * 超级管理员权限码
     */
    public static final String PERM_ADMIN_CODE = "*:*:*";

    /**
     * 根接口路径
     */
    public static final String ROOT_PATH = "/**";

    /**
     * SWAGGER接口路径
     */
    public static final String[] SWAGGER_PATH = {"/swagger-ui.html", "/swagger-ui/**", "/favicon.ico", "/v3/api-docs/**", "/webjars/**", "/doc.html", "/favicon.ico"};

    /**
     * 系统管理接口路径
     */
    public static final String SYS_PATH = "/sys/**";

    /**
     * 获取用户信息接口路径
     */
    public static final String SYS_USER_INFO_PATH = "/sys/user/getCurrentUser";

    /**
     * 日志管理接口路径
     */
    public static final String LOG_PATH = "/log/**";

    /**
     * 配置管理接口路径
     */
    public static final String CONF_PATH = "/conf/**";

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
