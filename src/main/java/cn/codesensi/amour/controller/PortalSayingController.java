package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.SayingConverter;
import cn.codesensi.amour.model.dto.SayingResultDTO;
import cn.codesensi.amour.model.response.SayingResponse;
import cn.codesensi.amour.service.SayingService;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户一言相关接口 前端控制器
 * <p>
 * 面向门户免登录场景，提供顶栏一言文案（随机优先，失败降级）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
public class PortalSayingController {

    private final SayingService sayingService;

    private final SayingConverter sayingConverter;

    /**
     * 查询一言（免登录）
     * <p>
     * 优先调用随机一言接口，失败降级一言接口；两级上游均不可用时返回空对象
     *
     * @return 一言文案与出处、作者；字段可能为空，由前端判空决定是否展示
     */
    @SaIgnore
    @GetMapping("/portal/saying")
    public SayingResponse portalSaying() {
        SayingResultDTO sayingResultDTO = sayingService.getSaying();
        return sayingConverter.toResponse(sayingResultDTO);
    }
}
