package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改系统配置请求参数。
 * <p>
 * 仅允许修改配置值；配置键、值类型、分组与状态由代码侧（{@code ConfigKeyEnum}）
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
     * 配置值(统一字符串存储；格式由服务端按值类型校验)。
     * <p>
     * 允许为空：图片型配置（站点 logo 等）清空即回退消费端兜底图；
     * 空值的格式约束由服务端按值类型校验兜底（布尔/整数类型不接受空值）
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "配置值长度不能超过" + AppConst.MAX_LENGTH_512)
    private String configValue;

}