package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * QQ 信息响应 —— 门户留言等场景的 QQ 头像与昵称查询结果。
 * <p>
 * 字段允许为 {@code null}：上游服务部分失败时以空值表达缺失，
 * 由调用方（前端）执行本地兜底（本地生成头像、手动填写昵称）。
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
     * QQ 头像地址（qq-service 解析的真实图片地址，强制 https；降级时为随机头像地址）
     */
    private String avatarUrl;

    /**
     * QQ 昵称（仅 qq-service 解析成功时返回，降级随机头像时为 null）
     */
    private String nickname;

}
