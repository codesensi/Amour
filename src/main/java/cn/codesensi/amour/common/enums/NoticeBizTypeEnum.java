package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通知业务类型枚举 —— 触发通知的业务事件类型，与 sys_notice.biz_type 及前端通知中心取值一致。
 * <p>
 * 说明：biz_type 是代码级关联键（与 biz_id 联合定位业务数据），
 * 暂不维护进 sys_dict（通知中心无标签展示诉求，待有诉求时再补 notice-biz-type 字典）。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum NoticeBizTypeEnum implements BaseEnum<String> {

    MESSAGE_AUDIT("message-audit", "留言审核"),
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
