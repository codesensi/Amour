package cn.codesensi.amour.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置分页查询行数据响应结果
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class ConfigPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值(统一字符串存储)
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
     * 是否敏感: 0-否, 1-是（敏感配置不经过免登录配置下发接口）
     */
    private Integer sensitive;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
