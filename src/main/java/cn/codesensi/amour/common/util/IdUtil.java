package cn.codesensi.amour.common.util;

import java.util.UUID;

public class IdUtil {

    // 私有构造器，防止实例化
    private IdUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    public static String fastSimpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}