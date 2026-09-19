package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RegexConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增字典类型请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictTypeInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码（kebab-case，如 gender、menu-type）
     */
    @NotBlank(message = "字典编码不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "字典编码长度不能超过" + AppConst.MAX_LENGTH_64)
    @Pattern(regexp = RegexConst.KEBAB_CASE, message = "字典编码仅允许小写字母、数字与中划线")
    private String dictCode;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "字典名称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String dictName;

    /**
     * 备注
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "备注长度不能超过" + AppConst.MAX_LENGTH_512)
    private String remark;

}
