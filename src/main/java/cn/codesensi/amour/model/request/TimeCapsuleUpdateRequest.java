package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修改时间胶囊请求参数（按 id 覆盖全部可编辑字段；
 * 显隐不经本接口维护，单独走 change-hidden 端点）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimeCapsuleUpdateRequest extends TimeCapsuleInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 胶囊ID
     */
    @NotNull(message = "胶囊ID不能为空")
    private Long id;

}
