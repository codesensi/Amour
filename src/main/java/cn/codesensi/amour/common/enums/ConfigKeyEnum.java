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
    CAPTCHA_IMAGE_TYPE("captcha.image-type", "图形验证码类型"),
    CAPTCHA_IMAGE_EXPIRE("captcha.image-expire", "图形验证码过期秒"),

    // ---------- 门户站点配置（site 分组：/site/config 门户展示配置的数据源） ----------
    SITE_SLOGAN("site.slogan", "门户站点标语"),
    SITE_FEMALE_NAME("site.female-name", "门户女主昵称"),
    SITE_MALE_NAME("site.male-name", "门户男主昵称"),
    SITE_FEMALE_QQ("site.female-qq", "门户女主 QQ 号"),
    SITE_MALE_QQ("site.male-qq", "门户男主 QQ 号"),
    SITE_LOVE_START_DATE("site.love-start-date", "门户恋爱计时起点"),
    SITE_ICP_TEXT("site.icp-text", "门户 ICP 备案文案"),
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
