package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门户纪念日条目行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * 字段语义与 {@code portal_anniversary} 表列一一对应。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class AnniversaryDTO implements AuditUserAware, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

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
     * 显隐标识: 0-显示, 1-隐藏（门户侧恒为显示）
     */
    private Integer hidden;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人用户ID（未登录来源的记录为空）
     */
    private Long creator;

    /**
     * 更新人用户ID（未发生过更新的记录为空）
     */
    private Long updater;

    /**
     * 创建人用户名（服务层按本页 creator/updater 批量回填）
     */
    private String creatorName;

    /**
     * 更新人用户名（服务层按本页 creator/updater 批量回填）
     */
    private String updaterName;

}
