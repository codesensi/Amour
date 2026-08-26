package cn.codesensi.amour.enums;

import cn.codesensi.amour.consts.AppConst;
import lombok.Getter;

/**
 * 删除标识枚举
 * 1-已删除
 * 0-未删除
 */
@Getter
public enum DelFlagEnum implements BaseEnum<Integer> {

    DELETED(AppConst.ONE_INT, "已删除"),
    NOT_DELETED(AppConst.ZERO_INT, "未删除"),
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
    DelFlagEnum(Integer code, String desc) {
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
