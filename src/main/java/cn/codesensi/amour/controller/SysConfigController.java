package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.ConfigConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.dto.ConfigPageDTO;
import cn.codesensi.amour.model.dto.ConfigUpdateDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.model.request.ConfigPageRequest;
import cn.codesensi.amour.model.request.ConfigUpdateRequest;
import cn.codesensi.amour.model.response.ConfigPageResponse;
import cn.codesensi.amour.model.response.ConfigResponse;
import cn.codesensi.amour.service.SysConfigService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 分页查询系统配置（登录态;管理端列表数据源,含禁用条目与完整字段,直查库不走缓存）。
     * <p>配置键为模糊匹配,分组与状态为精确匹配,条件缺省时自动忽略。
     *
     * @param configPageRequest 分页查询参数
     * @return 配置分页结果
     */
    @SaCheckPermission("system:config:page")
    @GetMapping("/page")
    public Page<ConfigPageResponse> page(@Valid ConfigPageRequest configPageRequest) {
        ConfigPageDTO pageDTO = configConverter.toPageDTO(configPageRequest);
        Page<SysConfig> configPage = sysConfigService.page(pageDTO);
        return configConverter.toPageResponse(configPage);
    }

    /**
     * 修改系统配置（仅允许修改配置值;更新后失效对应配置键缓存,热更新即时生效）。
     *
     * @param updateRequest 修改请求参数
     */
    @SaCheckPermission("system:config:update")
    @PutMapping("/update")
    public void update(@Valid @RequestBody ConfigUpdateRequest updateRequest) {
        ConfigUpdateDTO updateDTO = configConverter.toUpdateDTO(updateRequest);
        sysConfigService.update(updateDTO);
    }

    /**
     * 刷新配置缓存（清空全部 config 缓存,下次读取时回源查库）。
     */
    @SaCheckPermission("system:config:update")
    @PostMapping("/refresh-cache")
    public void refreshCache() {
        sysConfigService.evictCache(List.of());
    }
}
