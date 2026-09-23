package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 新增情侣日记参数 DTO（userId 由服务层取当前登录人填充）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DiaryInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录日期
     */
    private LocalDate diaryDate;

    /**
     * 心情标识（unknown-不标记,与 DiaryMoodEnum 对齐;服务层归一化缺省值）
     */
    private String mood;

    /**
     * 日记内容
     */
    private String content;

}
