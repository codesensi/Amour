package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.ConfigConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.response.ConfigResponse;
import cn.codesensi.amour.service.SysConfigService;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统公共配置相关接口 前端控制器
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/config")
public class SysConfigController {

    private final SysConfigService sysConfigService;
    private final ConfigConverter configConverter;

    /**
     * 获取站点公共配置（免登录；按 keys 指定的配置键下发，keys 为空时返回空列表）
     *
     * @param keys 配置键集合（逗号分隔，如 keys=name,captcha.enabled）；为空时返回空列表
     * @return 配置键值列表
     */
    @SaIgnore
    @GetMapping("/list-by-keys")
    public List<ConfigResponse> listByKeys(@RequestParam(value = "keys", required = false) List<String> keys) {
        List<ConfigDTO> configDTOs = sysConfigService.listByKeys(keys);
        return configConverter.toListResponse(configDTOs);
    }
}
