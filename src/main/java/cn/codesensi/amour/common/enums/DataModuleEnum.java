package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据权限模块枚举 —— 数据范围策略（sys_role_data_scope）的资源维度。
 * <p>
 * code 与管理端权限点的资源段对齐（如 admin:time-capsule:* 对应 TIME_CAPSULE），
 * 新增纳入数据隔离的业务模块时仅需补充枚举项并同步 data-module 字典；
 * 留言簿（访客创建无归属语义）与系统资源（无行归属概念）不纳入数据范围管控。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum DataModuleEnum implements BaseEnum<String> {

    TIME_CAPSULE("time-capsule", "时间胶囊"),
    DIARY("diary", "日记"),
    MOMENTS("moments", "点滴"),
    LOVE_PHOTO("love-photo", "恋爱画册"),
    LOVE_LIST("love-list", "恋爱清单"),
    FOOTPRINT("footprint", "足迹"),
    ANNIVERSARY("anniversary", "纪念日"),
    FILE("file", "文件"),
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
