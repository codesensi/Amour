package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public enum ConfigKeyEnum implements BaseEnum<String> {

    // ---------- 基础配置（base 分组：1000 段） ----------
    NAME("name", "项目/站点名称"),
    ICP("icp", "ICP备案文案"),
    COPYRIGHT_YEAR("copyright-year", "版权年份"),
    UAPI_KEY("uapi-key", "UApiPro接口密钥"),

    // ---------- 门户站点配置（site 分组：2000 段） ----------
    SITE_LOVE_START_DATE("site.love-start-date", "门户恋爱计时起点"),

    // ---------- 验证码配置（captcha 分组：3000 段） ----------
    CAPTCHA_ENABLED("captcha.enabled", "验证码开关"),
    CAPTCHA_IMAGE_TYPE("captcha.image-type", "图形验证码类型"),

    // ---------- 文件配置（file 分组：4000 段） ----------
    FILE_STORAGE("file.storage", "文件存储方式");

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;
}
