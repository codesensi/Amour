package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 点点滴滴修改参数 DTO（独立对象,不继承新增;id 必填,作者归属不可变）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 文章内容（富文本 HTML）
     */
    private String content;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 文章标签（逗号分隔）
     */
    private String tags;

    /**
     * 排序
     */
    private Integer sort;

}
