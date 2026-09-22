package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.constraint.InEnum;
import cn.codesensi.amour.common.enums.MessageAuditStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言审核请求参数。
 * <p>
 * 审核状态仅允许「通过/驳回」，待审核为提交后的初始态，不允许手动回设
 * （回设值由 Service 层以枚举口径校验拦截）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MessageAuditRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 留言ID
     */
    @NotNull(message = "留言ID不能为空")
    private Long id;

    /**
     * 审核状态: approved-通过， rejected-驳回
     */
    @NotNull(message = "审核状态不能为空")
    @InEnum(enumClass = MessageAuditStatusEnum.class, message = "审核状态不合法")
    private String auditStatus;

}
