package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 一言响应 —— 门户顶栏一言展示数据。
 * <p>
 * 随机一言解析成功时 content/source/author 齐全；降级 uapi-saying 时仅 content 有值；
 * 两级上游均未配置或均失败时字段全为 {@code null}，由前端不展示一言角标。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class SayingResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 一言文案（随机一言正文，降级时为 uapi-saying 文案）
     */
    private String content;

    /**
     * 出处（仅随机一言解析成功时返回，降级时为 null）
     */
    private String source;

    /**
     * 作者（仅随机一言解析成功时返回，降级时为 null）
     */
    private String author;

}
