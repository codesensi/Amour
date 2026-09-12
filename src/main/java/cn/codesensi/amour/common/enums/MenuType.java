package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 菜单类型枚举
 * D-目录
 * M-菜单
 * B-按钮
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum MenuType implements BaseEnum<String> {

    D("D", "目录"),
    M("M", "菜单"),
    B("B", "按钮"),
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
