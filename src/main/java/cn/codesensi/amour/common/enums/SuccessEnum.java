package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 成功/失败状态枚举
 * 1-成功
 * 0-失败
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum SuccessEnum implements BaseEnum<Integer> {

    SUCCESS(AppConst.ONE_INT, "成功"),
    FAIL(AppConst.ZERO_INT, "失败"),
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
