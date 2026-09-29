package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 定时任务触发方式枚举。
 * <p>
 * cron-cron调度
 * manual-手动执行
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum TriggerTypeEnum implements BaseEnum<String> {

    CRON("cron", "cron调度"),
    MANUAL("manual", "手动执行"),
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
