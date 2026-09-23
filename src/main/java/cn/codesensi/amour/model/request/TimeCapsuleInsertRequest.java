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
 * 新增时间胶囊请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class TimeCapsuleInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    @NotNull(message = "显隐标识不能为空")
    private Integer hidden;

}
