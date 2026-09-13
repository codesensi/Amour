package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.ConfigConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.response.ConfigResponse;
import cn.codesensi.amour.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门户站点公共配置相关接口 前端控制器
 * <p>
 * 面向门户免登录场景，按需下发站点展示类配置（站点名称、备案文案、验证码开关等）；
 * 敏感配置（{@code sensitive=1}，如 uapi-key）在服务层被剔除，不经过该接口。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalConfigController {

    private final SysConfigService sysConfigService;
    private final ConfigConverter configConverter;

    /**
     * 获取站点公共配置（免登录；按 keys 指定的配置键下发，keys 为空时返回空列表）。
     * <p>仅下发非敏感配置（{@code sensitive=0}），敏感配置的表现与"键不存在"一致。
     *
     * @param keys 配置键集合（逗号分隔，如 keys=name,captcha.enabled）；为空时返回空列表
     * @return 配置键值列表
     */
    @GetMapping("/config/list-by-keys")
    public List<ConfigResponse> listByKeys(@RequestParam(value = "keys", required = false) List<String> keys) {
        List<ConfigDTO> configDTOs = sysConfigService.listByKeysPublic(keys);
        return configConverter.toListResponse(configDTOs);
    }
}
