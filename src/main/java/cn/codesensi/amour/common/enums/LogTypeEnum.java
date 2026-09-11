package cn.codesensi.amour.common.enums;

import lombok.Getter;

/**
 * 日志类型枚举
 * <p>
 * 取值与 {@code sys_log.log_type} 列的字典注释一一对应。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
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
     * 枚举构造函数
     *
     * @param code 编码
     * @param desc 说明
     */
    LogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
