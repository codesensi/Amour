package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 允许/禁止状态枚举（通用，不带业务语义，如定时任务的并发开关）。
 *
 * 1-允许
 * 0-禁止
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum PermitEnum implements BaseEnum<Integer> {

    ALLOW(AppConst.ONE_INT, "允许"),
    FORBID(AppConst.ZERO_INT, "禁止"),
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
