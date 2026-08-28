package cn.codesensi.amour.common.enums;

import lombok.Getter;

/**
 * 图形验证码类型枚举
 * spec-PNG字符验证码
 * gif-GIF字符验证码
 * chinese-中文字符验证码
 * chinese-gif-中文GIF字符验证码
 * arithmetic-算术验证码
 */
@Getter
public enum ImageType implements BaseEnum<String> {

    SPEC("spec", "PNG字符验证码"),
    GIF("gif", "GIF字符验证码"),
    CHINESE("chinese", "中文字符验证码"),
    CHINESE_GIF("chinese-gif", "中文GIF字符验证码"),
    ARITHMETIC("arithmetic", "算术验证码"),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

    /**
     * 枚举构造函数
     *
     * @param code 编码
     * @param desc 说明
     */
    ImageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
