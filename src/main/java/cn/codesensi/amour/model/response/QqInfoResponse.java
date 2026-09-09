package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * QQ 信息响应 —— 门户留言等场景的 QQ 头像与昵称查询结果。
 * <p>
 * 服务端已完成降级：头像优先取 qq-api 解析的真实地址（强制 https），
 * 失败时降级为 qq-avatar 按 QQ 号拼接的地址，仅在 qq-avatar 未配置时为 {@code null}；
 * 昵称仅 qq-api 解析成功时返回，否则为 {@code null}（由调用方提示手动填写）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class QqInfoResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * QQ 头像地址（qq-api 解析的真实图片地址，强制 https；降级时为 qq-avatar 按 QQ 号拼接地址）
     */
    private String avatarUrl;

    /**
     * QQ 昵称（仅 qq-api 解析成功时返回，降级时为 null）
     */
    private String nickname;

}
