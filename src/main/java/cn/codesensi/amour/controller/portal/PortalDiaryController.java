package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.converter.DiaryConverter;
import cn.codesensi.amour.model.dto.DiaryDTO;
import cn.codesensi.amour.model.response.PortalDiaryResponse;
import cn.codesensi.amour.service.DiaryService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 情侣日记门户相关接口 前端控制器（免登录）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal/diary")
public class PortalDiaryController {

    private final DiaryService diaryService;

    private final DiaryConverter diaryConverter;

    /**
     * 门户情侣日记分页（免登录）。
     * <p>
     * 按记录日期降序 → id 降序，双人日记由前端按记录人分栏展示。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 情侣日记分页结果
     */
    @GetMapping("/page")
    public Page<PortalDiaryResponse> page(BasePage page) {
        Page<DiaryDTO> itemPage = diaryService.pagePortal(page);
        return diaryConverter.toPortalPage(itemPage);
    }
}
