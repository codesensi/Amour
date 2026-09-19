package cn.codesensi.amour.controller.portal;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.HeroConverter;
import cn.codesensi.amour.model.dto.HeroResultDTO;
import cn.codesensi.amour.model.response.HeroResponse;
import cn.codesensi.amour.service.HeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户主角相关接口 前端控制器。
 * <p>
 * 面向门户免登录场景，提供首屏男女主展示信息。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalHeroController {

    private final HeroService heroService;

    private final HeroConverter heroConverter;

    /**
     * 查询门户男女主（免登录）
     * <p>
     * 男女主取自 hero 角色绑定的启用用户（同性别最晚注册的一个）；
     * QQ、头像等字段可能为空，由前端按展示链路兜底
     *
     * @return 男主与女主信息；某性别暂无主角用户时对应字段为 null
     */
    @GetMapping("/hero")
    public HeroResponse portalHero() {
        HeroResultDTO heroResultDTO = heroService.getPortalHero();
        return heroConverter.toResponse(heroResultDTO);
    }
}
