package cn.codesensi.amour.common.enums;

import cn.codesensi.amour.common.consts.AppConst;
import lombok.Getter;

/**
 * 成功/失败状态枚举
 * 1-成功
 * 0-失败
 */
@Getter
public enum SuccessEnum implements BaseEnum<Integer> {

    SUCCESS(AppConst.ONE_INT, "成功"),
    FAIL(AppConst.ZERO_INT, "失败"),
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
    SuccessEnum(Integer code, String desc) {
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
