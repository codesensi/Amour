package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改字典类型请求参数
 * <p>
 * 字典编码与内置标识均不可修改，不在可提交字段之列。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictTypeUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    @NotNull(message = "字典类型ID不能为空")
    private Long id;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 64, message = "字典名称长度不能超过64")
    private String dictName;

    /**
     * 备注
     */
    @Size(max = AppConst.REMARK_MAX_LENGTH, message = "备注长度不能超过" + AppConst.REMARK_MAX_LENGTH)
    private String remark;

}
