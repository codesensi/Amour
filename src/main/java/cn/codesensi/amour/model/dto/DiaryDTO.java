package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 情侣日记行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * nickname/avatar 为记录人展示信息，由服务层按 {@code userId} 批量查用户表回填
 * （{@link AuthorInfoAware} 契约）；审计用户名沿用 {@link AuditUserAware} 契约
 * 由 {@code AuditUserFiller} 回填。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DiaryDTO implements AuditUserAware, AuthorInfoAware, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 记录人ID（双人日记按人分栏）
     */
    private Long userId;

    /**
     * 记录人用户名（服务层按 userId 批量查用户表回填）
     */
    private String username;

    /**
     * 记录人昵称（服务层批量回填;前端展示链路 QQ 昵称 → 昵称 → 用户名）
     */
    private String nickname;

    /**
     * 记录人 QQ 号（服务层批量回填;已维护时前端走 QQ 头像链路）
     */
    private String qq;

    /**
     * 记录人头像（服务层批量回填;空则前端兜底图）
     */
    private String avatar;

    /**
     * 记录日期
     */
    private LocalDate diaryDate;

    /**
     * 心情标识（unknown-不标记,与 DiaryMoodEnum 对齐）
     */
    private String mood;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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
