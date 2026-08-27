package cn.codesensi.amour.enums;

/**
 * 通用枚举接口，提供 {@link #getCode()} 与 {@link #getDesc()} 两个基本维度，
 * 供业务枚举统一实现，便于以统一的 code 语义在前后端之间传递与反查。
 *
 * @param <T> code 的类型（如 {@link Integer}、{@link String}）
 */
public interface BaseEnum<T> {

    /**
     * 获取编码。
     *
     * @return 编码值
     */
    T getCode();

    /**
     * 获取说明。
     *
     * @return 说明文本
     */
    String getDesc();

    /**
     * 根据 code 反查枚举实例（工具方法）。
     * <p>
     * 遍历 {@code enumClass} 的所有枚举常量，返回第一个 code 与给定 {@code code} 相等的实例；
     * 未找到或 {@code code} 为 null 时返回 {@code null}。
     *
     * @param enumClass 目标枚举类（须实现 {@link BaseEnum}）
     * @param code      待匹配的编码；可为 null（此时返回 null）
     * @param <E>       枚举类型
     * @param <T>       code 的类型
     * @return code 匹配的枚举实例；未匹配时返回 null
     */
    static <E extends Enum<E> & BaseEnum<T>, T> E fromCode(Class<E> enumClass, T code) {
        if (code == null) return null;
        for (E e : enumClass.getEnumConstants()) {
            if (code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }
}
