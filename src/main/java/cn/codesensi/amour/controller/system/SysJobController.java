package cn.codesensi.amour.controller.system;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.SysJobConverter;
import cn.codesensi.amour.model.dto.SysJobDTO;
import cn.codesensi.amour.model.dto.SysJobLogDTO;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.SysJobLogPageResponse;
import cn.codesensi.amour.model.response.SysJobPageResponse;
import cn.codesensi.amour.service.SysJobService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务管理 前端控制器（任务定义维护、启停/手动执行与执行日志查询）。
 *
 * @author codesensi
 * @since 1.0
 */
@Validated
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/job")
public class SysJobController {

    private final SysJobService sysJobService;

    private final SysJobConverter sysJobConverter;

    /**
     * 任务分页查询（附带每个任务最近一次执行结果）。
     *
     * @param request 分页与筛选参数（任务名/分组/状态均可空）
     * @return 任务分页
     */
    @SaCheckPermission("system:job:page")
    @GetMapping("/page")
    public Page<SysJobPageResponse> page(SysJobPageRequest request) {
        Page<SysJobDTO> dtoPage = sysJobService.page(sysJobConverter.toPageDTO(request));
        return sysJobConverter.toPageResponse(dtoPage);
    }

    /**
     * 新增任务（校验调用目标白名单与 cron 合法性，落库后立即注册调度）。
     *
     * @param insertRequest 新增参数
     */
    @SaCheckPermission("system:job:add")
    @Log(module = "定时任务", operation = "新增任务", type = LogTypeEnum.INSERT)
    @PostMapping("/insert")
    public void insert(@RequestBody @Valid SysJobInsertRequest insertRequest) {
        sysJobService.insert(sysJobConverter.toInsertDTO(insertRequest));
    }

    /**
     * 修改任务（cron 变更时热更新调度）。
     *
     * @param updateRequest 修改参数
     */
    @SaCheckPermission("system:job:update")
    @Log(module = "定时任务", operation = "修改任务", type = LogTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@RequestBody @Valid SysJobUpdateRequest updateRequest) {
        sysJobService.update(sysJobConverter.toUpdateDTO(updateRequest));
    }

    /**
     * 启动/暂停任务。
     *
     * @param changeStatusRequest 启停参数
     */
    @SaCheckPermission("system:job:change-status")
    @Log(module = "定时任务", operation = "启停任务", type = LogTypeEnum.UPDATE)
    @PutMapping("/change-status")
    public void changeStatus(@RequestBody @Valid SysJobChangeStatusRequest changeStatusRequest) {
        sysJobService.changeStatus(sysJobConverter.toChangeStatusDTO(changeStatusRequest));
    }

    /**
     * 删除任务（内置任务拒绝删除，执行日志跟随清理）。
     *
     * @param ids 任务ID集合（雪花ID字符串化路径参数）
     */
    @SaCheckPermission("system:job:delete")
    @Log(module = "定时任务", operation = "删除任务", type = LogTypeEnum.DELETE)
    @DeleteMapping("/delete/{ids}")
    public void delete(@PathVariable("ids") List<Long> ids) {
        sysJobService.delete(ids);
    }

    /**
     * 立即手动执行一次任务（异步执行，结果见执行日志）。
     *
     * @param runRequest 执行参数
     */
    @SaCheckPermission("system:job:run")
    @Log(module = "定时任务", operation = "执行任务", type = LogTypeEnum.UPDATE)
    @PutMapping("/run")
    public void run(@RequestBody @Valid SysJobRunRequest runRequest) {
        sysJobService.run(sysJobConverter.toRunDTO(runRequest));
    }

    /**
     * 执行日志分页查询（jobId 可空，空时查全部任务日志）。
     *
     * @param request 分页与筛选参数（任务ID/执行状态均可空）
     * @return 日志分页
     */
    @SaCheckPermission("system:job:page")
    @GetMapping("/log/page")
    public Page<SysJobLogPageResponse> logPage(SysJobLogPageRequest request) {
        Page<SysJobLogDTO> dtoPage = sysJobService.logPage(sysJobConverter.toLogPageDTO(request));
        return sysJobConverter.toLogPageResponse(dtoPage);
    }

    /**
     * 预览 cron 表达式的后续触发时间（新增/修改时人工校对）。
     *
     * @param cron cron 表达式
     * @return 后续触发时间列表（最多 5 次）
     */
    @SaCheckPermission("system:job:page")
    @GetMapping("/next-trigger-times")
    public List<LocalDateTime> nextTriggerTimes(
            @RequestParam @NotBlank(message = "cron 表达式不能为空") @Size(max = 64, message = "cron 表达式长度不能超过64") String cron) {
        return sysJobService.nextTriggerTimes(cron);
    }

}
