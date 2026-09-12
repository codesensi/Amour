package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文件存储类型枚举
 * local-本地磁盘
 * oss-对象存储
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
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
}
