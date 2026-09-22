package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言审核参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MessageAuditDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 留言ID
     */
    private Long id;

    /**
     * 审核状态: approved-通过， rejected-驳回
     */
    private String auditStatus;

}
