package cn.codesensi.amour.common.consts;

/**
 * 通用正则 —— 项目内跨模块复用的格式校验表达式，
 * 供 {@code @Pattern} 注解与代码内 {@code Pattern#compile} 统一引用，禁止散落字面量；
 * 校验提示语（message）由调用方在注解上指定，此处不承载文案。
 *
 * @author codesensi
 * @since 1.0
 */
public class RegexConst {

    /**
     * 空串或 15 位数字、17 位数字+校验位（数字或 X）共 18 位
     */
    public static final String EMPTY_OR_15_18_DIGITS = "^$|^\\d{15}$|^\\d{17}[\\dXx]$";

    /**
     * 空串或 1 开头共 11 位数字
     */
    public static final String EMPTY_OR_1_START_11_DIGITS = "^$|^1[3-9]\\d{9}$";

    /**
     * 空串或 6-12 位数字
     */
    public static final String EMPTY_OR_6_12_DIGITS = "^$|^[0-9]{6,12}$";

    /**
     * 小写字母、数字与中划线组成的 kebab-case 分段
     */
    public static final String KEBAB_CASE = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    /**
     * 4-2-2 数字段、中划线连接（yyyy-MM-dd）
     */
    public static final String DIGITS_4_2_2 = "^\\d{4}-\\d{2}-\\d{2}$";

    /**
     * 4-2-2 数字段、空格连接、2-2-2 数字段（yyyy-MM-dd HH:mm:ss）
     */
    public static final String DIGITS_4_2_2_SPACE_2_2_2 = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    /**
     * 字面前缀 /file/view/ 后接数字段
     */
    public static final String LITERALS_FILE_VIEW_DIGITS = "^/file/view/(\\d+)$";
}

