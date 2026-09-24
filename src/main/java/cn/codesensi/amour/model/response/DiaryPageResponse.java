package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 情侣日记行响应结果（管理端分页）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DiaryPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 记录人ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long userId;

    /**
     * 记录人用户名（服务层批量回填）
     */
    private String username;

    /**
     * 记录人头像（服务层批量回填;空则前端兜底图）
     */
    private String avatar;

    /**
     * 记录日期（yyyy-MM-dd）
     */
    private LocalDate diaryDate;

    /**
     * 心情标识（sunny/rainy/starry;空为未标记）
     */
    private String mood;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 创建时间（经全局 Jackson 配置序列化为 yyyy-MM-dd HH:mm:ss）
     */
    private LocalDateTime createTime;

    /**
     * 创建人用户名（服务层批量回填）
     */
    private String creatorName;

    /**
     * 更新人用户名（服务层批量回填;未发生过更新的记录为空）
     */
    private String updaterName;

}
