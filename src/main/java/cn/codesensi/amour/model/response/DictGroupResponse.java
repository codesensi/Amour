package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据字典分组查询响应结果 —— 单个字典编码及其组内的字典项列表。
 * <p>
 * 组内列表按 sort 升序，字典值统一为字符串，调用侧可依据业务自行完成类型转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictGroupResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码（如 gender、enable）
     */
    private String dictCode;

    /**
     * 组内字典项列表（按 sort 升序）
     */
    private List<DictResponse> items;

}
