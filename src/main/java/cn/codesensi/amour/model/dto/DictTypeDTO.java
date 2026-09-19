package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典类型 DTO —— 字典类型概要（含组内条目数）。
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
     * 字典类型ID
     */
    private Long id;

    /**
     * 字典编码（如 gender、enable）
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 是否内置:0-否，1-是（内置类型禁删、编码不可改）
     */
    private Integer builtin;

    /**
     * 该编码下的条目数（含禁用条目）
     */
    private Integer count;

    /**
     * 备注
     */
    private String remark;

}
