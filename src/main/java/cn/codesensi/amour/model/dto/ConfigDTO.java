package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置 DTO —— 面向配置查询结果的数据传输对象。
 * <p>
 * 携带配置的键、值、值类型与分组信息；配置值统一为字符串，
 * 调用侧可依据 {@code vType} 自行完成类型转换。
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
     * 配置键（app 之下的点分路径，如 captcha.sms-expire）
     */
    private String cKey;

    /**
     * 配置值（统一字符串存储）
     */
    private String cValue;

    /**
     * 值类型:STRING,INTEGER,LONG,BOOLEAN
     */
    private String vType;

    /**
     * 分组（app 的一级子项，如 name、captcha 等）
     */
    private String cGroup;

}
