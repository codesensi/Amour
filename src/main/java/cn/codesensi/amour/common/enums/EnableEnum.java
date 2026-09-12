package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 启用/禁用状态枚举
 * 0-启用
 * 1-禁用
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum EnableEnum implements BaseEnum<Integer> {

    ENABLE(AppConst.ZERO_INT, "启用"),
    DISABLE(AppConst.ONE_INT, "禁用"),
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
