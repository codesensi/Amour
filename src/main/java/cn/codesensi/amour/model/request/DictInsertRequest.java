package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增字典条目请求参数
 * <p>
 * 字典名称即类型名（组内共享），由后端自动继承该编码组内已有条目的名称，不在可提交字段之列。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码（kebab-case，如 gender、menu-type）
     */
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 64, message = "字典编码长度不能超过64")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "字典编码仅允许小写字母、数字与中划线")
    private String dictCode;

    /**
     * 字典值（统一字符串存储）
     */
    @NotBlank(message = "字典值不能为空")
    @Size(max = 128, message = "字典值长度不能超过128")
    private String dictValue;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 128, message = "字典标签长度不能超过128")
    private String dictLabel;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 状态:0-启用，1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 512, message = "备注长度不能超过512")
    private String remark;

}