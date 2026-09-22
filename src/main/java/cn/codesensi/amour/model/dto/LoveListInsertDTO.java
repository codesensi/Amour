package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增恋爱清单项参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoveListInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清单内容
     */
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    private Integer done;

    /**
     * 纪念照地址（完成项可选）
     */
    private String photo;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
