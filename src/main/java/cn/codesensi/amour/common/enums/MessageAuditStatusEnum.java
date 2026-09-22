package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 留言审核状态枚举（code 为英文单词，与字典 message-audit-status 及库内取值一致）。
 *
 * pending-待审核
 * approved-通过
 * rejected-驳回
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum MessageAuditStatusEnum implements BaseEnum<String> {

    PENDING("pending", "待审核"),
    APPROVED("approved", "通过"),
    REJECTED("rejected", "驳回"),
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
