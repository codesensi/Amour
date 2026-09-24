package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 点点滴滴历史分类/标签建议 DTO —— Service 层出参（去重后下发）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsHistoryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 历史分类（去重）
     */
    private List<String> categories;

    /**
     * 历史标签（逗号拆分后去重）
     */
    private List<String> tags;

}
