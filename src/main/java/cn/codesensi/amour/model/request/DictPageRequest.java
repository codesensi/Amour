package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典分页查询请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DictPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码(模糊匹配)
     */
    @Size(max = 64, message = "字典编码长度不能超过64")
    private String dictCode;

    /**
     * 字典名称(模糊匹配)
     */
    @Size(max = 64, message = "字典名称长度不能超过64")
    private String dictName;

    /**
     * 字典值(模糊匹配)
     */
    @Size(max = 128, message = "字典值长度不能超过128")
    private String dictValue;

    /**
     * 状态:0-启用,1-禁用
     */
    private Integer status;

}
