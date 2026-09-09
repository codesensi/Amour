package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典分页查询参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DictPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码(模糊匹配)
     */
    private String dictCode;

    /**
     * 字典名称(模糊匹配)
     */
    private String dictName;

    /**
     * 字典值(模糊匹配)
     */
    private String dictValue;

    /**
     * 状态:0-启用，1-禁用
     */
    private Integer status;

}