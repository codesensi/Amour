package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 新增情侣日记请求参数（记录人由服务层取当前登录人填充,不接收 userId）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DiaryInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录日期
     */
    @NotNull(message = "记录日期不能为空")
    private LocalDate diaryDate;

    /**
     * 心情标识（unknown-不标记,与 DiaryMoodEnum 对齐;缺省默认 unknown）
     */
    @Size(max = AppConst.MAX_LENGTH_16, message = "心情标识长度不能超过" + AppConst.MAX_LENGTH_16)
    private String mood;

    /**
     * 日记内容
     */
    @NotBlank(message = "日记内容不能为空")
    @Size(max = AppConst.MAX_LENGTH_5000, message = "日记内容长度不能超过" + AppConst.MAX_LENGTH_5000)
    private String content;

}
