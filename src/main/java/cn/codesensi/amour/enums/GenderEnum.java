package cn.codesensi.amour.enums;

import lombok.Getter;

/**
 * 性别枚举
 * U-未知
 * M-男
 * F-女
 */
@Getter
public enum GenderEnum implements BaseEnum<String> {

    UNKNOWN("U", "未知"),
    MALE("M", "男"),
    FEMALE("F", "女"),
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
    GenderEnum(String code, String desc) {
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
