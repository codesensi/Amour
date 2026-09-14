package cn.codesensi.amour.common.consts;

/**
 * 缓存常量 —— 项目内缓存层使用的公共常量。
 * <p>
 * 缓存名已枚举化（见 {@link cn.codesensi.amour.common.enums.CacheNameEnum}），
 * 本类仅保留与具体缓存无关的公共常量。
 *
 * @author codesensi
 * @since 1.0
 */
public class CacheConst {

    /**
     * 缓存空值哨兵：Caffeine 不允许缓存 {@code null}，用该哨兵占位表示"数据不存在"，
     * 读取时再还原为 {@code null}，从而使"不存在"的结果也能被缓存，避免反复回源。
     */
    public static final Object NULL_MARKER = new Object();

}
