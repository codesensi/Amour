package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置 DTO —— 面向配置查询结果的数据传输对象。
 * <p>
 * 携带配置的键、值、值类型与分组信息；配置值统一为字符串，
 * 调用侧可依据 {@code valueType} 自行完成类型转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class ConfigDTO implements Serializable {

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

    /**
     * 是否敏感: 0-否, 1-是（敏感配置仅供服务端内部消费，不经过免登录配置下发接口）
     */
    private Integer sensitive;

}
