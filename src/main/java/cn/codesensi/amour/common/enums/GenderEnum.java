package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 性别枚举
 * U-未知
 * M-男
 * F-女
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
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
}
