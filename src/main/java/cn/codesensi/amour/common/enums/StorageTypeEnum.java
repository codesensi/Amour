package cn.codesensi.amour.common.enums;

import lombok.Getter;

/**
 * 文件存储类型枚举
 * local-本地磁盘
 * oss-对象存储
 *
 * @since 1.0
 */
@Getter
public enum StorageTypeEnum implements BaseEnum<String> {

    LOCAL("local", "本地存储"),
    OSS("oss", "对象存储"),
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
    StorageTypeEnum(String code, String desc) {
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
