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

    // ---------- 基础配置（base 分组：1000 段） ----------
    NAME("name", "项目/站点名称"),
    ICP_TEXT("icp-text", "ICP备案文案"),
    COPYRIGHT_YEAR("copyright-year", "版权年份"),
    QQ_SERVICE("qq-service", "用户QQ头像服务地址"),
    AVATAR_SERVICE("avatar-service", "用户随机头像服务地址"),

    // ---------- 门户站点配置（site 分组：2000 段） ----------
    SITE_TITLE("site.title", "门户站点标题"),
    SITE_SLOGAN("site.slogan", "门户站点标语"),
    SITE_LOVE_START_DATE("site.love-start-date", "门户恋爱计时起点"),

    // ---------- 验证码配置（captcha 分组：3000 段） ----------
    CAPTCHA_ENABLED("captcha.enabled", "验证码开关"),
    CAPTCHA_IMAGE_TYPE("captcha.image-type", "图形验证码类型"),
    CAPTCHA_IMAGE_EXPIRE("captcha.image-expire", "图形验证码过期秒"),
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
