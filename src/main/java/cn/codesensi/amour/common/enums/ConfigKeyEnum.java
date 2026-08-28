package cn.codesensi.amour.common.enums;

import lombok.Getter;

/**
 * 系统配置键枚举 —— 与 sys_config 表初始化数据（sql/init_dml.sql）中的配置键一一对应。
 * <p>
 * 统一以枚举常量引用配置键，避免调用侧散落魔法字符串；
 * 新增配置时请同步补充初始化 DML 脚本与本枚举。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
public enum ConfigKeyEnum implements BaseEnum<String> {

    NAME("name", "项目名称"),
    VERSION("version", "版本号"),
    AUTHOR("author", "负责人"),
    COPYRIGHT("copyright", "版权年份"),
    AVATAR("avatar", "用户随机头像服务地址"),
    DEMO_MODE("demo-mode", "演示模式开关"),
    CAPTCHA_ENABLED("captcha.enabled", "验证码开关"),
    CAPTCHA_TYPE("captcha.type", "验证码类型"),
    CAPTCHA_IMAGE_TYPE("captcha.image-type", "图形验证码类型"),
    CAPTCHA_IMAGE_EXPIRE("captcha.image-expire", "图形验证码过期秒"),
    CAPTCHA_SMS_EXPIRE("captcha.sms-expire", "短信验证码过期秒"),
    CAPTCHA_SMS_LENGTH("captcha.sms-length", "短信验证码长度"),
    ;

    /**
     * 编码
     */
    private final String code;

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
    ConfigKeyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
