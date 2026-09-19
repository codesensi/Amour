package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典类型查询响应结果 —— 单个字典编码的概要信息。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class DictTypeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
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
