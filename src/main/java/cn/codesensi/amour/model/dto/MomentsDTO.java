package cn.codesensi.amour.model.dto;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 点点滴滴行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * 作者展示信息由服务层按 {@code userId} 批量查用户表回填（{@link AuthorInfoAware} 契约）；
 * 审计用户名沿用 {@link AuditUserAware} 契约由 {@code AuditUserFiller} 回填。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsDTO implements AuditUserAware, AuthorInfoAware, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
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
     * 作者用户名（服务层按 userId 批量查用户表回填）
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
     * 记录日期
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间（创建即写入,后续编辑时刷新）
     */
    private LocalDateTime updateTime;

    /**
     * 创建人用户ID
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
