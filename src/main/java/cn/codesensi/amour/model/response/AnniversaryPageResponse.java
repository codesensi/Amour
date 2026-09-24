package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 纪念日行响应结果（管理端分页）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class AnniversaryPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 纪念日名称
     */
    private String name;

    /**
     * 纪念日类型（字典 anniversary-type 编码）
     */
    private String type;

    /**
     * 纪念日日期（yyyy-MM-dd；每年重复时仅月/日生效）
     */
    private String anniversaryDate;

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

    /**
     * 创建时间（yyyy-MM-dd HH:mm:ss，经 JacksonConfig 全局格式输出）
     */
    private LocalDateTime createTime;

    /**
     * 创建人用户名（服务层批量回填;未登录来源记录为空）
     */
    private String creatorName;

    /**
     * 更新人用户名（服务层批量回填;未发生过更新的记录为空）
     */
    private String updaterName;

}
