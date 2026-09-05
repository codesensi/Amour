package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.PortalHeroConverter;
import cn.codesensi.amour.model.dto.PortalHeroResultDTO;
import cn.codesensi.amour.model.response.PortalHeroResponse;
import cn.codesensi.amour.service.PortalHeroService;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户主角相关接口 前端控制器
 * <p>
 * 面向门户免登录场景，提供首屏男女主展示信息。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
public class PortalHeroController {

    private final PortalHeroService portalHeroService;

    private final PortalHeroConverter portalHeroConverter;

    /**
     * 查询门户男女主（免登录）
     * <p>
     * 男女主取自 hero 角色绑定的启用用户（同性别最晚注册的一个）；
     * QQ、头像等字段可能为空，由前端按展示链路兜底
     *
     * @return 男主与女主信息；某性别暂无主角用户时对应字段为 null
     */
    @SaIgnore
    @GetMapping("/portal/hero")
    public PortalHeroResponse portalHero() {
        PortalHeroResultDTO portalHeroResultDTO = portalHeroService.getPortalHero();
        return portalHeroConverter.toResponse(portalHeroResultDTO);
    }
}
