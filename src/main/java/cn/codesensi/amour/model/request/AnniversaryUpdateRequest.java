package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RegexConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改纪念日请求参数（按 id 覆盖全部可编辑字段）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class AnniversaryUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "纪念日ID不能为空")
    private Long id;

    /**
     * 纪念日名称
     */
    @NotBlank(message = "纪念日名称不能为空")
    @Size(max = AppConst.MAX_LENGTH_128, message = "纪念日名称长度不能超过" + AppConst.MAX_LENGTH_128)
    private String name;

    /**
     * 纪念日类型（字典 anniversary-type 编码，如 birthday/anniversary/festival）
     */
    @NotBlank(message = "纪念日类型不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "纪念日类型长度不能超过" + AppConst.MAX_LENGTH_64)
    private String type;

    /**
     * 纪念日日期（格式:yyyy-MM-dd）
     */
    @NotBlank(message = "纪念日日期不能为空")
    @Pattern(regexp = RegexConst.DIGITS_4_2_2, message = "纪念日日期格式须为yyyy-MM-dd")
    private String anniversaryDate;

    /**
     * 是否每年重复: true-是, false-否
     */
    @NotNull(message = "是否每年重复不能为空")
    private Boolean repeatYearly;

    /**
     * 排序（数字越小越靠前；显隐走独立 change-hidden 端点）
     */
    private Integer sort;

}
