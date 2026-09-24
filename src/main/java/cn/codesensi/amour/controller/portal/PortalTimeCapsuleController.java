package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.TimeCapsuleConverter;
import cn.codesensi.amour.model.dto.TimeCapsuleDTO;
import cn.codesensi.amour.model.response.PortalTimeCapsuleResponse;
import cn.codesensi.amour.service.TimeCapsuleService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 时间胶囊门户相关接口 前端控制器（免登录）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/time-capsule")
public class PortalTimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    private final TimeCapsuleConverter timeCapsuleConverter;

    /**
     * 门户时间胶囊分页（免登录）。
     * <p>
     * 仅返回显隐为「显示」的胶囊，排序为解锁时间升序 → id 升序；
     * 未到解锁时间的记录 content 为 null，unlocked 标识是否已解锁。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 时间胶囊分页结果
     */
    @GetMapping("/page")
    public Page<PortalTimeCapsuleResponse> page(@Valid BasePage page) {
        Page<TimeCapsuleDTO> itemPage = timeCapsuleService.pagePortal(page);
        return timeCapsuleConverter.toPortalPage(itemPage);
    }
}
