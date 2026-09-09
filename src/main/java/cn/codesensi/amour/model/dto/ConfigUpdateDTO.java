package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置修改 DTO —— 面向管理端修改配置的参数载体。
 * <p>
 * 仅允许修改配置值；配置键、值类型、分组与状态由代码侧约定，不可变更。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class ConfigUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 配置值(统一字符串存储；格式由服务端按值类型校验)
     */
    private String configValue;

}