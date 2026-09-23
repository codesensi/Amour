package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 情侣日记分页查询请求参数（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DiaryPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录人ID（精确匹配）
     */
    private Long userId;

    /**
     * 记录日期（精确匹配,yyyy-MM-dd）
     */
    private LocalDate diaryDate;

    /**
     * 心情标识（精确匹配;sunny/rainy/starry）
     */
    @Size(max = 16, message = "心情标识长度不能超过16")
    private String mood;

}
