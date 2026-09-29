package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 定时任务分组枚举。
 * <p>
 * default-默认
 * infra-基础设施
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum JobGroupEnum implements BaseEnum<String> {

    DEFAULT("default", "默认"),
    INFRA("infra", "基础设施"),
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
