package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 门户主角 DTO —— 门户首屏展示的男女主信息。
 * <p>
 * 男女主取自绑定了 hero 角色的启用用户：同性别按创建时间倒序取最晚注册的一个；
 * 同性别暂无主角用户时对应字段为 {@code null}。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class PortalHeroResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 男主信息（无启用的男性主角用户时为 null）
     */
    private PortalHeroUserDTO male;

    /**
     * 女主信息（无启用的女性主角用户时为 null）
     */
    private PortalHeroUserDTO female;

}
