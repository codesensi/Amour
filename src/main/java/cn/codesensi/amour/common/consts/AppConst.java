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
     * 字符串列长度档位 —— 与 init_ddl.sql 各 VARCHAR 列宽一一对应，
     * 校验注解按所在列宽引用对应档位，DDL 调整列宽时仅需修改对应档位数值。
     * <p>
     * 档位对应关系：16 → phone/qq/config_group；64 → username/password/nickname/email/
     * role.name 与 code/menu.title/perms/dict_code/dict_name/config_key/photo.tags；
     * 128 → dict_value/dict_label；256 → menu.path/component/icon/file.original_name；
     * 512 → msg/remark/avatar/config_value/photo.url 与 caption
     */
    public static final int MAX_LENGTH_16 = 16;

    /**
     * 字符串列长度档位 64
     */
    public static final int MAX_LENGTH_64 = 64;

    /**
     * 字符串列长度档位 128
     */
    public static final int MAX_LENGTH_128 = 128;

    /**
     * 字符串列长度档位 256
     */
    public static final int MAX_LENGTH_256 = 256;

    /**
     * 字符串列长度档位 512
     */
    public static final int MAX_LENGTH_512 = 512;

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
