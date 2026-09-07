package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改系统配置请求参数
 * <p>
 * 仅允许修改配置值、状态与备注；配置键、值类型与分组由代码侧（{@code ConfigKeyEnum}）
 * 与初始化脚本约定，不接受修改。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class ConfigUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "配置ID不能为空")
    private Long id;

    /**
     * 配置值(统一字符串存储;格式由服务端按值类型校验)
     */
    @NotBlank(message = "配置值不能为空")
    @Size(max = 4000, message = "配置值长度不能超过4000")
    private String configValue;

    /**
     * 配置状态:0-启用,1-禁用
     */
    @NotNull(message = "配置状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 512, message = "备注长度不能超过512")
    private String remark;

}
