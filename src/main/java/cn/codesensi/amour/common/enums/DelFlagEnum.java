package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 删除标识枚举
 * 1-已删除
 * 0-未删除
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum DelFlagEnum implements BaseEnum<Integer> {

    NOT_DELETED(AppConst.ZERO_INT, "未删除"),
    DELETED(AppConst.ONE_INT, "已删除"),
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
