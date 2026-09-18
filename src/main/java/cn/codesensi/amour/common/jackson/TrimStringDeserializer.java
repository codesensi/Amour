package cn.codesensi.amour.common.jackson;

import cn.hutool.core.util.StrUtil;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * String 反序列化器：反序列化时剥除首尾空白，剥空后置 null。
 * <p>
 * 全局生效于所有 {@code @RequestBody} 接口：空串/纯空格统一为 null（库中不落空串），
 * 且置空发生在 {@code @Valid} 校验之前，空格无法绕过必填校验。
 *
 * @author codesensi
 * @since 1.0
 */
public class TrimStringDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) {
        return StrUtil.trimToNull(p.getValueAsString());
    }
}
