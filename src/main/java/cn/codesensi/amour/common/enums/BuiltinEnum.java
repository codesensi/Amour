package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;

/**
 * 系统内置标识枚举
 * 1-内置
 * 0-非内置
 */
@Getter
public enum BuiltinEnum implements BaseEnum<Integer> {

    YES(AppConst.ONE_INT, "内置"),
    NO(AppConst.ZERO_INT, "非内置"),
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
    BuiltinEnum(Integer code, String desc) {
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
