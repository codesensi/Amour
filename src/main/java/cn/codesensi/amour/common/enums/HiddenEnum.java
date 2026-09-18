package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 照片显隐枚举
 * 0-显示
 * 1-隐藏
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum HiddenEnum implements BaseEnum<Integer> {

    SHOW(AppConst.ZERO_INT, "显示"),
    HIDDEN(AppConst.ONE_INT, "隐藏"),
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
