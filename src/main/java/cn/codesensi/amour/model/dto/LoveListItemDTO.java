package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 恋爱清单项行数据 DTO —— Service 层出参，门户与管理端共用。
 * <p>
 * 字段语义与 {@code portal_love_list} 表列一一对应；
 * 门户侧由 {@code LoveListConverter} 重命名映射为前端契约（content→text、photo→img），
 * 管理端侧同名直映为分页行响应。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoveListItemDTO implements AuditUserAware, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 清单内容
     */
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    private Integer done;

    /**
     * 纪念照地址（完成项可选）
     */
    private String photo;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示， 1-隐藏
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
