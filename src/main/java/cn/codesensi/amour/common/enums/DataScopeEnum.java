package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据范围档位枚举 —— 数据范围策略的取值（可见/可改共用一套档位）。
 * <p>
 * 合法组合满足一致性不变式：可改范围不宽于可见范围，即
 * all/all（全管）、all/self（看全改己）、self/self（各管各的）三种。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum DataScopeEnum implements BaseEnum<String> {

    /**
     * 全部数据：不限制行归属
     */
    ALL("all", "全部数据"),

    /**
     * 仅本人：仅行创建人（creator）本人可见/可改
     */
    SELF("self", "仅本人"),
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
