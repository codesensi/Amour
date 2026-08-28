package cn.codesensi.amour.common.util;

import java.util.UUID;

/**
 * ID 生成工具类。
 * <p>
 * 纯静态工具类，私有构造器防止实例化。
 */
public class IdUtil {

    // 私有构造器，防止实例化
    private IdUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 生成不带连字符的随机 UUID（32 位十六进制字符串）。
     * <p>
     * 例如 {@code 3f8a9c1e2b7d4e5fa0b1c2d3e4f5a6b7}，常用于生成 TraceId 等场景。
     *
     * @return 32 位不带连字符的 UUID 字符串
     */
    public static String fastSimpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
