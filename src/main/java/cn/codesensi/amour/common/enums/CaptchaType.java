package cn.codesensi.amour.common.enums;

import lombok.Getter;

/**
 * 验证码生成类型枚举
 * sms-短信验证码
 * image-图形验证码
 */
@Getter
public enum CaptchaType implements BaseEnum<String> {

    SMS("sms", "短信验证码"),
    IMAGE("image", "图形验证码"),
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
    CaptchaType(String code, String desc) {
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
