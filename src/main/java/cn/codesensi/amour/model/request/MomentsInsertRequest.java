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
 * 点点滴滴新增请求参数。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MomentsInsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = AppConst.MAX_LENGTH_256, message = "标题长度不能超过" + AppConst.MAX_LENGTH_256)
    private String title;

    /**
     * 记录日期
     */
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /**
     * 文章内容（富文本 HTML）
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 文章分类（自由输入）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "分类长度不能超过" + AppConst.MAX_LENGTH_64)
    private String category;

    /**
     * 文章标签（逗号分隔,自由输入）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "标签长度不能超过" + AppConst.MAX_LENGTH_64)
    private String tags;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 状态: 0-显示， 1-隐藏（仅新增表单使用,修改经 change-status 端点维护）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

}
