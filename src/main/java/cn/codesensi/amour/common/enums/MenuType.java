package cn.codesensi.amour.common.enums;

import lombok.Getter;

/**
 * 菜单类型枚举
 * D-目录
 * M-菜单
 * B-按钮
 */
@Getter
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

    /**
     * 枚举构造函数
     *
     * @param code 编码
     * @param desc 说明
     */
    MenuType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
