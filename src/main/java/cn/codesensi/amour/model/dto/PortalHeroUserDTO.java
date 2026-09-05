package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 门户主角用户 DTO —— 单个主角（男主或女主）的展示信息。
 * <p>
 * 字段允许为 {@code null}：用户未维护 QQ 或未上传头像时以空值表达缺失。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class PortalHeroUserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户头像地址（用户表上传头像，未维护时为 null）
     */
    private String avatar;

    /**
     * 用户QQ号码（未维护时为 null）
     */
    private String qq;

}
