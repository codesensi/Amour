package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 一言上游响应 DTO —— 对应 UApiPro 随机一言接口（uapi-saying-random）的 JSON 形态。
 * <p>
 * 仅映射本服务关注的字段，上游返回的其余字段（如 uuid、corpus、category）自动忽略。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class SayingResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 一言文案
     */
    private String content;

    /**
     * 出处
     */
    private String source;

    /**
     * 作者
     */
    private String author;

}
