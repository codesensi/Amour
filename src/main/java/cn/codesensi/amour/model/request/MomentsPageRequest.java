package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 点点滴滴分页查询请求（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MomentsPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章标题（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_256, message = "标题长度不能超过" + AppConst.MAX_LENGTH_256)
    private String title;

    /**
     * 文章分类（精确匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "分类长度不能超过" + AppConst.MAX_LENGTH_64)
    private String category;

    /**
     * 状态（精确匹配）
     */
    private Integer status;

}
