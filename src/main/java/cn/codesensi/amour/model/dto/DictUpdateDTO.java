package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改字典条目业务数据
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典条目ID
     */
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典值
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

    /**
     * 状态:0-启用，1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}