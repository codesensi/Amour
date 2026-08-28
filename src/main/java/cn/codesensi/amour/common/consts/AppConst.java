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
     * 链路追踪 ID 在 MDC 中的键名
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 默认密码（初始账号的明文密码，仅供初始化数据使用）
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 用户上下文标识
     */
    public static final String USER_CONTEXT = "userContext";

    /**
     * MDC上下文标识
     */
    public static final String MDC_CONTEXT = "mdcContext";

}
