package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置分页查询请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConfigPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置键(模糊匹配)
     */
    @Size(max = 128, message = "配置键长度不能超过128")
    private String configKey;

    /**
     * 分组
     */
    @Size(max = 64, message = "分组长度不能超过64")
    private String configGroup;

    /**
     * 配置状态:0-启用,1-禁用
     */
    private Integer status;

}
