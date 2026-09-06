package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置查询响应结果。
 * <p>
 * 配置值统一为字符串，调用侧可依据 {@code valueType} 自行完成类型转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class ConfigResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值（统一字符串存储）
     */
    private String configValue;

    /**
     * 值类型:STRING,INTEGER,LONG,BOOLEAN
     */
    private String valueType;

    /**
     * 分组（app 的一级子项，如 name、captcha 等）
     */
    private String configGroup;

}
