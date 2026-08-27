package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;

/**
 * 启用/禁用状态枚举
 * 0-启用
 * 1-禁用
 */
@Getter
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

    /**
     * 枚举构造函数
     *
     * @param code 编码
     * @param desc 说明
     */
    EnableEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
