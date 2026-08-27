package cn.codesensi.amour.common.consts;

import cn.codesensi.amour.common.util.CacheUtil;

/**
 * 缓存常量
 */
public class CacheConst {

    /**
     * 验证码缓存名（基础缓存名，实际使用时经 {@link CacheUtil#withAppEnv(String)} 拼接项目名_运行环境前缀）
     */
    public static final String CAPTCHA = "captcha";

    /**
     * 系统配置缓存名（基础缓存名，实际使用时经 {@link CacheUtil#withAppEnv(String)} 拼接项目名_运行环境前缀）
     */
    public static final String CONFIG = "config";

}
