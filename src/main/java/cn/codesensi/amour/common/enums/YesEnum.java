package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 是或否枚举
 * 1-是
 * 0-否
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum YesEnum implements BaseEnum<Integer> {

    YES(AppConst.ONE_INT, "是"),
    NO(AppConst.ZERO_INT, "否"),
    ;

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 说明
     */
    private final String desc;
}
