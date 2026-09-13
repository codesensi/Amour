package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.LogConverter;
import cn.codesensi.amour.model.request.LogPageRequest;
import cn.codesensi.amour.model.response.LogPageResponse;
import cn.codesensi.amour.service.SysLogService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统日志相关接口 前端控制器。
 * <p>
 * 登录日志与操作日志同表存储（sys_log.log_type 区分），按两个端点分别下发，
 * 权限码与菜单按钮一一对应。
 *
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/log")
public class SysLogController {

    private final SysLogService sysLogService;
    private final LogConverter logConverter;

    /**
     * 分页查询登录日志（含登录与登出记录）。
     *
     * @param request 分页查询参数
     * @return 登录日志分页结果
     */
    @SaCheckPermission("log:login:page")
    @GetMapping("/login/page")
    public Page<LogPageResponse> loginPage(@Valid LogPageRequest request) {
        return logConverter.toPageResponse(sysLogService.page(logConverter.toPageDTO(request),
                List.of(LogTypeEnum.LOGIN.getCode(), LogTypeEnum.LOGOUT.getCode())));
    }

    /**
     * 分页查询操作日志（除登录/登出外的全部操作类型）。
     *
     * @param request 分页查询参数
     * @return 操作日志分页结果
     */
    @SaCheckPermission("log:operate:page")
    @GetMapping("/operate/page")
    public Page<LogPageResponse> operatePage(@Valid LogPageRequest request) {
        return logConverter.toPageResponse(sysLogService.page(logConverter.toPageDTO(request),
                List.of(LogTypeEnum.QUERY.getCode(), LogTypeEnum.INSERT.getCode(),
                        LogTypeEnum.UPDATE.getCode(), LogTypeEnum.DELETE.getCode(),
                        LogTypeEnum.GRANT.getCode(), LogTypeEnum.UPLOAD.getCode(),
                        LogTypeEnum.DOWNLOAD.getCode())));
    }
}
