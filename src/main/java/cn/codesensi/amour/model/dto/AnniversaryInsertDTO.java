package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 新增纪念日参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class AnniversaryInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 纪念日名称
     */
    private String name;

    /**
     * 纪念日类型（字典 anniversary-type，与 AnniversaryTypeEnum 编码对齐）
     */
    private String type;

    /**
     * 纪念日日期（每年重复时仅月/日生效）
     */
    private LocalDate anniversaryDate;

    /**
     * 是否每年重复: true-是, false-否
     */
    private Boolean repeatYearly;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示, 1-隐藏
     */
    private Integer hidden;

}
