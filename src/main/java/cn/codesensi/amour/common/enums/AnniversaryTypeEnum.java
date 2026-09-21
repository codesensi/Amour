package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 纪念日类型枚举。
 *
 * birthday-生日
 * anniversary-纪念日
 * festival-节日
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum AnniversaryTypeEnum implements BaseEnum<String> {

    BIRTHDAY("birthday", "生日"),
    ANNIVERSARY("anniversary", "纪念日"),
    FESTIVAL("festival", "节日"),
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
