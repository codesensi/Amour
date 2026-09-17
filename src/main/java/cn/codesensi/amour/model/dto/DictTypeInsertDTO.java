package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增字典类型业务数据。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictTypeInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码（kebab-case，如 gender、menu-type）
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 备注
     */
    private String remark;

}
