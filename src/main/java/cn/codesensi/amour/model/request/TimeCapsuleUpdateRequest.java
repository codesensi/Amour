package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修改时间胶囊请求参数（独立对象,不继承新增;按 id 覆盖全部可编辑字段；
 * 显隐不经本接口维护，单独走 change-hidden 端点）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class TimeCapsuleUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 胶囊ID
     */
    @NotNull(message = "胶囊ID不能为空")
    private Long id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = AppConst.MAX_LENGTH_128, message = "标题长度不能超过" + AppConst.MAX_LENGTH_128)
    private String title;

    /**
     * 信件内容
     */
    @NotBlank(message = "信件内容不能为空")
    @Size(max = AppConst.MAX_LENGTH_5000, message = "信件内容长度不能超过" + AppConst.MAX_LENGTH_5000)
    private String content;

    /**
     * 解锁时间（到点后门户可见全文）
     */
    @NotNull(message = "解锁时间不能为空")
    private LocalDateTime openTime;

}
