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
     * 门户主角角色标识
     */
    public static final String ROLE_HERO_CODE = "hero";

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
    public static final String LOGIN_PATH = "/login";

    /**
     * 退出登录接口路径
     */
    public static final String LOGOUT_PATH = "/logout";

    /**
     * 门户端接口路径前缀（门户端接口统一以 /portal 开头，面向访客免登录）
     */
    public static final String PORTAL_PATH = "/portal/**";

    /**
     * 文件预览接口路径前缀（img 等标签发起的资源请求不携带凭证，需免登录放行；仅覆盖读取，上传/下载不豁免）
     */
    public static final String FILE_VIEW_PATH = "/file/view/**";

    /**
     * H2 控制台路径前缀（仅 dev 启用 spring.h2.console.enabled，控制台自带 JDBC 账密页，免登录放行）
     */
    public static final String H2_CONSOLE_PATH = "/h2-console/**";

    /**
     * 公开路径清单 —— 验证码、登录、登出、门户 /portal/**、文件预览。
     * <p>
     * 鉴权与演示模式两个拦截器共用本清单统一放行，新增公开接口时仅需在此追加；
     * H2 控制台不在此列——仅鉴权拦截器额外豁免，演示模式拦截器保留对其写操作的拦截。
     */
    public static final String[] PUBLIC_PATHS = {
            CAPTCHA_PATH,
            LOGIN_PATH,
            LOGOUT_PATH,
            PORTAL_PATH,
            FILE_VIEW_PATH
    };

}
