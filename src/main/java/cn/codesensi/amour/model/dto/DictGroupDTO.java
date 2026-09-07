package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据字典分组 DTO —— 单个字典编码及其组内的字典项列表。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码（如 gender、enable）
     */
    private String dictCode;

    /**
     * 组内字典项列表（按 sort 升序）
     */
    private List<DictDTO> items;

}
