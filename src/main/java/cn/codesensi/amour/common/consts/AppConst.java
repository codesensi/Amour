package cn.codesensi.amour.common.consts;

/**
 * 通用常量 —— 项目内跨模块复用的基础常量（数值、字符串与上下文键名）。
 *
 * @author codesensi
 * @since 1.0
 */
public class AppConst {

    /**
     * Integer数字0
     */
    public static final Integer ZERO_INT = 0;

    /**
     * Integer数字1
     */
    public static final Integer ONE_INT = 1;

    /**
     * Long数字0
     */
    public static final Long ZERO_LONG = 0L;

    /**
     * Long数字1
     */
    public static final Long ONE_LONG = 1L;

    /**
     * 字符串0
     */
    public static final String ZERO_STR = "0";

    /**
     * 字符串1
     */
    public static final String ONE_STR = "1";

    /**
     * 消息字段（msg）统一最大长度 —— 项目内所有表的 msg 列与代码截断均对齐此值
     */
    public static final int MSG_MAX_LENGTH = 512;

    /**
     * 备注列宽 —— 各表 remark 列均为 VARCHAR(512)，校验注解对齐此值
     */
    public static final int REMARK_MAX_LENGTH = 512;

    /**
     * 用户头像地址列宽 —— avatar 列为 VARCHAR(512)，校验注解对齐此值
     */
    public static final int AVATAR_MAX_LENGTH = 512;

    /**
     * 路由路径列宽 —— sys_menu 的 path 列为 VARCHAR(512)，校验注解对齐此值
     */
    public static final int PATH_MAX_LENGTH = 512;

    /**
     * 用户账号列宽 —— sys_user 的 username 列为 VARCHAR(128)，登录与改名等校验注解对齐此值
     */
    public static final int USERNAME_MAX_LENGTH = 128;

    /**
     * 明文密码长度上限 —— 入库为加密值（sys_user.password VARCHAR(512)），登录与修改密码校验口径一致
     */
    public static final int PASSWORD_MAX_LENGTH = 64;

    /**
     * 链路追踪 ID 在 MDC 中的键名
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 默认密码（初始账号的明文密码，仅供初始化数据使用）
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * UApiPro 密钥请求头名（sys_config {@code uapi-key} 的值非空时携带）
     */
    public static final String UAPI_KEY_HEADER = "X-API-KEY";

    /**
     * 点号分隔符 —— sys_config 配置键分段（rate-limit.login.limit）、对象存储 key 扩展名等连接
     */
    public static final String DOT = ".";

    /**
     * 冒号分隔符 —— 限流缓存键（接口键:IP）等多段标识连接
     */
    public static final String COLON = ":";

    /**
     * 斜杠分隔符 —— 对象存储对象键的路径分段连接
     */
    public static final String SLASH = "/";

    /**
     * 下划线分隔符 —— 缓存名「项目名_运行环境」前缀拼接
     */
    public static final String UNDERSCORE = "_";

    /**
     * 逗号分隔符 —— 多值拆分（如 X-Forwarded-For 多 IP）与多值文案连接
     */
    public static final String COMMA = ",";

}
