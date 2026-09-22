package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改恋爱清单项请求参数（按 id 覆盖全部可编辑字段；
 * 显隐不经本接口维护，单独走 change-hidden 端点）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoveListUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清单项ID
     */
    @NotNull(message = "清单项ID不能为空")
    private Long id;

    /**
     * 清单内容
     */
    @NotBlank(message = "清单内容不能为空")
    @Size(max = AppConst.MAX_LENGTH_256, message = "清单内容长度不能超过" + AppConst.MAX_LENGTH_256)
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    @NotNull(message = "完成状态不能为空")
    private Integer done;

    /**
     * 纪念照地址（完成项可选）
     */
    @Size(max = AppConst.MAX_LENGTH_512, message = "纪念照地址长度不能超过" + AppConst.MAX_LENGTH_512)
    private String photo;

    /**
     * 排序（数字越小越靠前）
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
