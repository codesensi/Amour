package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 时间胶囊行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * 字段语义与 {@code portal_time_capsule} 表列一一对应；
 * 门户侧未到解锁时间的记录由服务层将 content 置空后下发（防抓包剧透），
 * 管理端侧同名直映为分页行响应。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class TimeCapsuleDTO implements AuditUserAware, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 信件内容
     */
    private String content;

    /**
     * 解锁时间（到点后门户可见全文）
     */
    private LocalDateTime openTime;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否已解锁（服务层按当前时间判定;仅门户口径消费）
     */
    private Boolean unlocked;

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
