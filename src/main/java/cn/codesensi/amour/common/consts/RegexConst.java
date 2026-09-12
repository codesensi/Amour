package cn.codesensi.amour.common.consts;

/**
 * 通用正则 —— 项目内跨模块复用的格式校验表达式，
 * 供 {@code @Pattern} 注解与代码内 {@code Pattern#compile} 统一引用，禁止散落字面量。
 *
 * @author codesensi
 * @since 1.0
 */
public class RegexConst {

    /**
     * 身份证号码:15 位或 18 位(末位可为 X/x)
     */
    public static final String ID_CARD = "^\\d{15}$|^\\d{17}[\\dXx]$";
    public static final String ID_CARD_MESSAGE = "身份证号码格式不正确";

    /**
     * 大陆手机号:1 开头 11 位
     */
    public static final String PHONE = "^1[3-9]\\d{9}$";
    public static final String PHONE_MESSAGE = "手机号格式不正确";

    /**
     * QQ 号码:6-12 位数字
     */
    public static final String QQ = "^[0-9]{6,12}$";
    public static final String QQ_MESSAGE = "QQ号码格式错误，请输入6-12位数字";

    /**
     * 字典编码:kebab-case(小写字母、数字、中划线)
     */
    public static final String DICT_CODE = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
    public static final String DICT_CODE_MESSAGE = "字典编码仅允许小写字母、数字与中划线";

    /**
     * DATETIME 值类型的合法形态(yyyy-MM-dd HH:mm:ss)
     */
    public static final String DATETIME_FORMAT = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    /**
     * 文件查看路由:/file/view/{id}
     */
    public static final String FILE_VIEW_URL = "^/file/view/(\\d+)$";

}
