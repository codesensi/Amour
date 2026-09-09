package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 一言降级接口响应 DTO —— 对应 UApiPro 一言接口（uapi-saying）的 JSON 形态。
 * <p>
 * 响应仅含 text 字段，映射为文案；出处与作者该接口不提供。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class SayingTextDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 一言文案
     */
    private String text;

}
