package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.MessageConverter;
import cn.codesensi.amour.model.dto.MessageDTO;
import cn.codesensi.amour.model.request.MessageAuditRequest;
import cn.codesensi.amour.model.request.MessagePageRequest;
import cn.codesensi.amour.model.response.MessagePageResponse;
import cn.codesensi.amour.service.MessageService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言簿管理相关接口 前端控制器。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/admin/message")
public class AdminMessageController {

    private final MessageService messageService;
    private final MessageConverter messageConverter;

    /**
     * 分页查询留言（管理端，完整字段，含待审核与驳回）。
     * <p>
     * 昵称为模糊匹配，审核状态为精确匹配，条件缺省时自动忽略；
     * 排序为留言时间降序 → id 降序。
     *
     * @param pageRequest 分页查询参数
     * @return 留言分页结果
     */
    @SaCheckPermission("admin:message:page")
    @GetMapping("/page")
    public Page<MessagePageResponse> page(@Valid MessagePageRequest pageRequest) {
        Page<MessageDTO> itemPage = messageService.pageAdmin(messageConverter.toPageDTO(pageRequest));
        return messageConverter.toPageResponse(itemPage);
    }

    /**
     * 审核留言（通过/驳回；待审核为提交后的初始态，不允许经本端点回设）。
     *
     * @param request 留言审核请求参数
     */
    @SaCheckPermission("admin:message:update")
    @Log(module = "留言簿", operation = "审核留言", type = LogTypeEnum.UPDATE)
    @PutMapping("/audit")
    public void audit(@Valid @RequestBody MessageAuditRequest request) {
        messageService.audit(messageConverter.toAuditDTO(request));
    }

    /**
     * 批量逻辑删除留言。
     *
     * @param ids 留言ID集合（雪花ID字符串化传输）
     */
    @SaCheckPermission("admin:message:delete")
    @Log(module = "留言簿", operation = "删除留言", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable Long[] ids) {
        messageService.delete(List.of(ids));
    }
}
