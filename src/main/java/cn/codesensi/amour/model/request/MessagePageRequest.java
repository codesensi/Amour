package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.constraint.InEnum;
import cn.codesensi.amour.common.enums.MessageAuditStatusEnum;
import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言分页查询请求参数（管理端）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MessagePageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访客昵称（模糊匹配）
     */
    @Size(max = AppConst.MAX_LENGTH_64, message = "昵称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String nickname;

    /**
     * 审核状态: pending-待审核， approved-通过， rejected-驳回
     */
    @InEnum(enumClass = MessageAuditStatusEnum.class, message = "审核状态不合法")
    private String auditStatus;

}
