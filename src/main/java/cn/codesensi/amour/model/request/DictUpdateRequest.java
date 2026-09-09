package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改字典条目请求参数
 * <p>
 * 字典编码、字典名称与内置标识均不可修改（字典名称即类型名，组内共享）；
 * 状态经启停接口（change-status）单独维护，均不在可提交字段之列。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典条目ID
     */
    @NotNull(message = "字典条目ID不能为空")
    private Long id;

    /**
     * 字典值（统一字符串存储；内置条目不允许修改）
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
     * 备注
     */
    @Size(max = 512, message = "备注长度不能超过512")
    private String remark;

}