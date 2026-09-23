package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改情侣日记请求参数（继承新增字段;记录人归属不可变）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiaryUpdateRequest extends DiaryInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日记ID
     */
    @NotNull(message = "日记ID不能为空")
    private Long id;

}
