package cn.codesensi.amour.util;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

/**
 * 对象与 JSON 字符串互转的工具类。
 * <p>
 * 基于 Jackson 3 的 {@link JsonMapper} 实现，提供序列化、反序列化以及
 * 类型安全的泛型转换能力。各方法在失败时抛出
 * {@link tools.jackson.core.JacksonException}（unchecked 运行时异常），
 * 调用方可按需捕获处理。
 */
public class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 内部共享的 JsonMapper 实例。
     */
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param object 待序列化对象，可为 null
     * @return JSON 字符串；对象为 null 时返回 "null"
     * @throws tools.jackson.core.JacksonException 序列化失败时抛出
     */
    public static String toJsonString(Object object) {
        return MAPPER.writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param json  JSON 字符串
     * @param clazz 目标对象的 Class
     * @param <T>   目标对象类型
     * @return 反序列化后的对象
     * @throws tools.jackson.core.JacksonException 反序列化失败时抛出
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return MAPPER.readValue(json, clazz);
    }

    /**
     * 将 JSON 字符串反序列化为带泛型的对象（如 List、Map 等）。
     *
     * <p>示例：
     * <pre>{@code
     * List<User> users = JsonUtil.parseObject(json, new TypeReference<List<User>>() {});
     * }</pre>
     *
     * @param json          JSON 字符串
     * @param typeReference 泛型类型描述
     * @param <T>           目标对象类型
     * @return 反序列化后的对象
     * @throws tools.jackson.core.JacksonException 反序列化失败时抛出
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        return MAPPER.readValue(json, typeReference);
    }

    /**
     * 将 JSON 字符串反序列化为 {@link List}。
     *
     * @param json     JSON 字符串
     * @param itemType 列表元素类型
     * @param <T>      列表元素类型
     * @return 反序列化后的 List
     * @throws tools.jackson.core.JacksonException 反序列化失败时抛出
     */
    public static <T> List<T> parseArray(String json, Class<T> itemType) {
        JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, itemType);
        return MAPPER.readValue(json, javaType);
    }

    /**
     * 将 JSON 字符串反序列化为 {@link Map}。
     *
     * @param json      JSON 字符串
     * @param keyType   key 类型
     * @param valueType value 类型
     * @param <K>       key 类型
     * @param <V>       value 类型
     * @return 反序列化后的 Map
     * @throws tools.jackson.core.JacksonException 反序列化失败时抛出
     */
    public static <K, V> Map<K, V> parseMap(String json, Class<K> keyType, Class<V> valueType) {
        JavaType javaType = MAPPER.getTypeFactory()
                .constructMapType(Map.class, keyType, valueType);
        return MAPPER.readValue(json, javaType);
    }

    /**
     * 将对象转换为泛型类型（如从 Map 转为某个实体）。
     *
     * @param object 待转换对象
     * @param type   目标类型
     * @param <T>    目标类型
     * @return 转换后的对象
     * @throws IllegalArgumentException 转换失败时抛出
     */
    public static <T> T convertValue(Object object, Class<T> type) {
        return MAPPER.convertValue(object, type);
    }

    /**
     * 将对象转换为带泛型的类型（如 List、Map 等）。
     *
     * @param object        待转换对象
     * @param typeReference 泛型类型描述
     * @param <T>           目标类型
     * @return 转换后的对象
     * @throws IllegalArgumentException 转换失败时抛出
     */
    public static <T> T convertValue(Object object, TypeReference<T> typeReference) {
        return MAPPER.convertValue(object, typeReference);
    }
}
