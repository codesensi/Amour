package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 配置值类型枚举。
 * <p>
 * 与 sys_config.value_type 及数据字典 config-value-type 的字面量一一对应，
 * 用于运行时配置值的格式校验分派，避免散落的字符串魔法值与库中字面量隐式耦合。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum ValueType implements BaseEnum<String> {

    /**
     * 字符串（不做格式限制，同时作为未知类型的兜底口径）
     */
    STRING("STRING", "字符串"),
    /**
     * 整数
     */
    INTEGER("INTEGER", "整数"),
    /**
     * 长整数
     */
    LONG("LONG", "长整数"),
    /**
     * 布尔（仅接受 true/false）
     */
    BOOLEAN("BOOLEAN", "布尔"),
    /**
     * 日期时间（yyyy-MM-dd HH:mm:ss）
     */
    DATETIME("DATETIME", "日期时间"),
    ;

    /**
     * 编码（与库中 value_type 字面量一致）
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

}
