package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 完成状态枚举（通用，不带业务语义，如恋爱清单的已完成/未完成）。
 *
 * 0-未完成
 * 1-已完成
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum DoneEnum implements BaseEnum<Integer> {

    UNDONE(AppConst.ZERO_INT, "未完成"),
    DONE(AppConst.ONE_INT, "已完成"),
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
