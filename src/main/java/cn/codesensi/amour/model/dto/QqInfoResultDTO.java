package cn.codesensi.amour.model.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * qq-service 上游响应 DTO —— 对应 JSON 形态的 QQ 信息接口（如 uapis.cn）。
 * <p>
 * 仅映射本服务关注的字段，上游返回的其余字段自动忽略；
 * 下划线字段经 {@link Alias @Alias} 映射为驼峰属性。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class QqInfoResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * QQ 昵称
     */
    private String nickname;

    /**
     * QQ 头像地址
     */
    private String avatarUrl;

}
