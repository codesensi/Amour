package cn.codesensi.amour.enums;

/**
 * 通用枚举接口，提供 code 和 desc
 */
public interface BaseEnum<T> {

    /**
     * 获取编码
     *
     * @return 编码
     */
    T getCode();

    /**
     * 获取说明
     *
     * @return 说明
     */
    String getDesc();

    /**
     * 根据 code 获取枚举实例（工具方法）
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
