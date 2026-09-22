package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.MessageAuditDTO;
import cn.codesensi.amour.model.dto.MessageDTO;
import cn.codesensi.amour.model.dto.MessagePageDTO;
import cn.codesensi.amour.model.dto.MessageSubmitDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 留言服务 —— 门户留言簿的提交与下发、管理端审核与删除共用。
 *
 * @author codesensi
 * @since 1.0
 */
public interface MessageService {

    /**
     * 门户留言分页（免登录）。
     *
     * @param page 分页参数
     * @return 审核通过的留言条目 DTO 分页结果
     */
    Page<MessageDTO> pagePortal(BasePage page);

    /**
     * 提交留言（免登录）。
     *
     * @param submitDTO 提交留言参数 DTO
     */
    void submit(MessageSubmitDTO submitDTO);

    /**
     * 管理端留言分页（全量，含待审核与驳回）。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 留言条目 DTO 分页结果
     */
    Page<MessageDTO> pageAdmin(MessagePageDTO pageDTO);

    /**
     * 审核留言（通过/驳回）。
     *
     * @param auditDTO 留言审核参数 DTO
     */
    void audit(MessageAuditDTO auditDTO);

    /**
     * 批量逻辑删除留言。
     *
     * @param ids 留言ID集合
     */
    void delete(List<Long> ids);

}
