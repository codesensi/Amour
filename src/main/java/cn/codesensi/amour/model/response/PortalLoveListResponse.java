package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 恋爱清单门户响应结果（免登录下发）。
 * <p>
 * 字段契约对齐前端门户（{@code api/portal/love-list.ts}）：text/img/done，
 * 由转换器从条目 DTO 重命名映射，门户页面与 mock 无需感知表字段名。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalLoveListResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清单文案
     */
    private String text;

    /**
     * 是否已完成
     */
    private Boolean done;

    /**
     * 可选照片（已完成项可带纪念照）
     */
    private String img;

}
