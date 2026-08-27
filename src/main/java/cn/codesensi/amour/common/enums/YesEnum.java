package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;

/**
 * 是或否枚举
 * 1-是
 * 0-否
 */
@Getter
public enum YesEnum implements BaseEnum<Integer> {

    YES(AppConst.ONE_INT, "是"),
    NO(AppConst.ZERO_INT, "否"),
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
    YesEnum(Integer code, String desc) {
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
