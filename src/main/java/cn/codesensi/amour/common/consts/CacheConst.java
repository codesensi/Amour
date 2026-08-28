package cn.codesensi.amour.common.consts;

import cn.codesensi.amour.common.util.CacheUtil;

/**
 * 缓存常量 —— 项目内缓存层使用的公共常量。
 *
 * @author codesensi
 * @since 1.0
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

    /**
     * 缓存空值哨兵：Caffeine 不允许缓存 {@code null}，用该哨兵占位表示"数据不存在"，
     * 读取时再还原为 {@code null}，从而使"不存在"的结果也能被缓存，避免反复回源。
     */
    public static final Object NULL_MARKER = new Object();

}
