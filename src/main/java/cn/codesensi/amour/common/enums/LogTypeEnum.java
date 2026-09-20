package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 日志类型枚举。
 * <p>
 * 取值与 {@code sys_log.log_type} 列的字典注释一一对应。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum LogTypeEnum implements BaseEnum<Integer> {

    UNKNOWN(0, "未知"),
    LOGIN(1, "登录"),
    LOGOUT(2, "登出"),
    QUERY(3, "查询"),
    INSERT(4, "新增"),
    UPDATE(5, "修改"),
    DELETE(6, "删除"),
    GRANT(7, "授权"),
    UPLOAD(8, "上传"),
    DOWNLOAD(9, "下载"),
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
     * 操作日志类型清单：除登录/登出与未知兜底外的全部操作类型。
     * <p>供操作日志分页端点下发查询范围，清单随枚举演进自动纳入新类型；
     * 排除 {@link #UNKNOWN} 与历史硬编码口径保持一致。
     *
     * @return 操作日志类型编码列表
     */
    public static List<Integer> operateTypes() {
        return Arrays.stream(values())
                .filter(type -> type != UNKNOWN && type != LOGIN && type != LOGOUT)
                .map(LogTypeEnum::getCode)
                .toList();
    }
}
