package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 门户恋爱画册照片响应结果。
 * <p>
 * 字段名对齐前端契约（img/text/date），由转换器从表字段语义重命名而来。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalLovePhotoResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 照片地址（前端画册 img 源）
     */
    private String img;

    /**
     * 照片文案
     */
    private String text;

    /**
     * 照片日期（yyyy-MM-dd）
     */
    private String date;

    /**
     * 照片标签集合（由逗号分隔存储拆分而来；无标签时空数组）
     */
    private List<String> tags;

}
