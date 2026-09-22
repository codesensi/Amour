package cn.codesensi.amour.model.dto;

/**
 * 审计用户回填契约 —— 行数据 DTO 实现本接口即可获得创建人/更新人用户名回填能力。
 * <p>
 * 由 {@code AuditUserFiller} 按本契约批量回填：DTO 携带 creator/updater 主键并提供
 * creatorName/updaterName 写入口，Service 层分页查询后调用一次回填即可。
 *
 * @author codesensi
 * @since 1.0
 */
public interface AuditUserAware {

    /**
     * 创建人用户ID（BaseEntity 审计字段，可空：未登录来源的记录无创建人）
     *
     * @return 创建人用户ID
     */
    Long getCreator();

    /**
     * 更新人用户ID（BaseEntity 审计字段，可空：未发生过更新的记录无更新人）
     *
     * @return 更新人用户ID
     */
    Long getUpdater();

    /**
     * 写入创建人用户名（由回填器填充）
     *
     * @param creatorName 创建人用户名
     */
    void setCreatorName(String creatorName);

    /**
     * 写入更新人用户名（由回填器填充）
     *
     * @param updaterName 更新人用户名
     */
    void setUpdaterName(String updaterName);

}
