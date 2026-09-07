package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置分页查询参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConfigPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置键(模糊匹配)
     */
    private String configKey;

    /**
     * 分组
     */
    private String configGroup;

    /**
     * 配置状态:0-启用,1-禁用
     */
    private Integer status;

}
