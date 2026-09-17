package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改字典类型业务数据。
 * <p>
 * 字典编码与内置标识不可修改，仅允许维护名称与备注。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictTypeUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 备注
     */
    private String remark;

}
