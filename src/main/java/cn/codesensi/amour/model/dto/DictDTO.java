package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典项 DTO —— 面向字典查询结果的数据传输对象。
 * <p>
 * 仅携带前端下拉框与枚举展示所需的展示层字段；字典值统一为字符串，
 * 调用侧（如前端下拉组件）可依据业务自行完成类型转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典值（统一字符串存储）
     */
    private String dictValue;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

}
