package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典类型 DTO —— 按字典编码聚合出的类型概要(单表扁平结构的分组视角)。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码(如 gender、enable)
     */
    private String dictCode;

    /**
     * 字典名称(组内任一行)
     */
    private String dictName;

    /**
     * 该编码下的条目数(含禁用条目)
     */
    private Integer count;

}
