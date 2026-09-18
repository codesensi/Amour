package cn.codesensi.amour.controller.admin;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.CacheConverter;
import cn.codesensi.amour.model.dto.CacheDTO;
import cn.codesensi.amour.model.response.CacheResponse;
import cn.codesensi.amour.service.SysCacheService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 缓存相关接口 前端控制器
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/cache")
public class SysCacheController {

    private final SysCacheService sysCacheService;
    private final CacheConverter cacheConverter;

    /**
     * 查询全部缓存内容
     */
    @SaCheckPermission("system:cache:list")
    @GetMapping("/list")
    public List<CacheResponse> list() {
        List<CacheDTO> cacheList = sysCacheService.list();
        return cacheConverter.toResponseList(cacheList);
    }
}
