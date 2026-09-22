package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改恋爱清单项参数 DTO（id 必填；显隐不经本 DTO 维护，单独走 change-hidden）。
 * <p>
 * 不继承新增 DTO：修改表单不含显隐字段，避免映射阶段出现未映射属性。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoveListUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清单项ID
     */
    private Long id;

    /**
     * 清单内容
     */
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    private Integer done;

    /**
     * 纪念照地址（完成项可选）
     */
    private String photo;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

}
