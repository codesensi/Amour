package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 修改情侣日记参数 DTO（id 必填;记录人归属不可变）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DiaryUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日记ID
     */
    private Long id;

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
