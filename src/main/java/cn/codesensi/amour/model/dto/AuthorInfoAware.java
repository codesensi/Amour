package cn.codesensi.amour.model.dto;

/**
 * 作者展示信息回填契约 —— 行数据 DTO 实现本接口即可获得按 userId 的
 * 用户名/昵称/QQ/头像批量回填能力。
 * <p>
 * 由 {@code AuthorInfoFiller} 按本契约批量回填：DTO 携带 userId 主键并提供
 * username/nickname/qq/avatar 写入口，Service 层分页查询后调用一次回填即可。
 * 与 {@link AuditUserAware}（审计语义,creator/updater → 用户名）相互独立：
 * 本契约面向门户前台作者展示链路,业务作者与记录创建人允许不同。
 *
 * @author codesensi
 * @since 1.0
 */
public interface AuthorInfoAware {

    /**
     * 作者用户ID（业务字段,可空）
     *
     * @return 作者用户ID
     */
    Long getUserId();

    /**
     * 写入作者用户名（由回填器填充）
     *
     * @param username 作者用户名
     */
    void setUsername(String username);

    /**
     * 写入作者昵称（由回填器填充）
     *
     * @param nickname 作者昵称
     */
    void setNickname(String nickname);

    /**
     * 写入作者 QQ 号（由回填器填充）
     *
     * @param qq 作者 QQ 号
     */
    void setQq(String qq);

    /**
     * 写入作者上传头像（由回填器填充）
     *
     * @param avatar 作者头像地址
     */
    void setAvatar(String avatar);

}
