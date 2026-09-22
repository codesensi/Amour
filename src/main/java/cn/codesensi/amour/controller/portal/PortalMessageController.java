package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.RateLimit;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.RateLimitKey;
import cn.codesensi.amour.model.converter.MessageConverter;
import cn.codesensi.amour.model.dto.MessageDTO;
import cn.codesensi.amour.model.dto.MessageSubmitDTO;
import cn.codesensi.amour.model.request.MessageSubmitRequest;
import cn.codesensi.amour.model.response.PortalMessageResponse;
import cn.codesensi.amour.service.MessageService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户留言簿相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供留言的上墙分页下发与访客提交
 * （/portal/** 已在 RbacConst 公开清单整体放行；提交防滥用依赖 @RateLimit 按 IP 限流）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/message")
public class PortalMessageController {

    private final MessageService messageService;
    private final MessageConverter messageConverter;

    /**
     * 查询留言分页（免登录）。
     *
     * @param page 分页参数（pageNumber/pageSize，页码 1 起且每页不超过 500）
     * @return 仅含审核通过的留言，按留言时间降序
     */
    @GetMapping
    public Page<PortalMessageResponse> page(@Valid BasePage page) {
        Page<MessageDTO> itemPage = messageService.pagePortal(page);
        return messageConverter.toPortalPage(itemPage);
    }

    /**
     * 提交留言（免登录；落库即待审核，审核通过后上墙）。
     * <p>
     * 头像由服务端按 qq 快照（fail-soft），IP 与归属地由服务端采集，均不由前端传入。
     *
     * @param submitRequest 提交留言请求参数
     */
    @PostMapping
    @RateLimit(key = RateLimitKey.MESSAGE, fallbackLimit = 3, fallbackWindowSeconds = 60)
    public void submit(@Valid @RequestBody MessageSubmitRequest submitRequest) {
        MessageSubmitDTO submitDTO = messageConverter.toSubmitDTO(submitRequest);
        messageService.submit(submitDTO);
    }
}
