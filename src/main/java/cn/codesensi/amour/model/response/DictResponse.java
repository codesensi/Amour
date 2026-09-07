package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典项查询响应结果。
 * <p>
 * 字典值统一为字符串，调用侧可依据业务自行完成类型转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictResponse implements Serializable {

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
