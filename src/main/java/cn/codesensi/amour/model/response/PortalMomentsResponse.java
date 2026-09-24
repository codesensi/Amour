package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 点点滴滴门户响应结果（免登录下发,仅显示状态的文章）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalMomentsResponse implements Serializable {

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
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long userId;

    /**
     * 作者用户名（服务层批量回填）
     */
    private String username;

    /**
     * 作者昵称（服务层批量回填;前端展示链路 QQ 昵称 → 昵称 → 用户名）
     */
    private String nickname;

    /**
     * 作者 QQ 号（服务层批量回填;已维护时前端走 QQ 头像链路）
     */
    private String qq;

    /**
     * 作者头像（服务层批量回填;空则前端兜底图）
     */
    private String avatar;

    /**
     * 文章内容（富文本 HTML）
     */
    private String content;

    /**
     * 记录日期（yyyy-MM-dd）
     */
    private LocalDate recordDate;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 文章标签（逗号分隔）
     */
    private String tags;

    /**
     * 最后更新时间（yyyy-MM-dd HH:mm:ss;创建即写入,后续编辑时刷新）
     */
    private LocalDateTime updateTime;

}
