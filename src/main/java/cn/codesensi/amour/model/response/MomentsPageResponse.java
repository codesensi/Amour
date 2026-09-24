package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 点点滴滴管理端行响应。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者ID
     */
    private Long userId;

    /**
     * 作者用户名（服务层批量回填）
     */
    private String username;

    /**
     * 文章内容（富文本 HTML）
     */
    private String content;

    /**
     * 记录日期（yyyy-MM-dd）
     */
    private LocalDate recordDate;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 文章标签（逗号分隔）
     */
    private String tags;

    /**
     * 状态（0-显示,1-隐藏）
     */
    private Integer status;

    /**
     * 创建时间（yyyy-MM-dd HH:mm:ss）
     */
    private LocalDateTime createTime;

}
