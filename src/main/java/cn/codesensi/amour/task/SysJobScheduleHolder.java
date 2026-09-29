package cn.codesensi.amour.task;

import cn.codesensi.amour.common.consts.ThreadConst;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.enums.PermitEnum;
import cn.codesensi.amour.common.enums.SuccessEnum;
import cn.codesensi.amour.common.enums.TriggerTypeEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.SysJobLogMapper;
import cn.codesensi.amour.mapper.SysJobMapper;
import cn.codesensi.amour.model.entity.SysJob;
import cn.codesensi.amour.model.entity.SysJobLog;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.zalando.logbook.autoconfigure.LogbookProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static cn.codesensi.amour.model.entity.table.SysJobTableDef.SYS_JOB;

/**
 * 定时任务调度容器。
 * <p>
 * 基于 Spring {@link ThreadPoolTaskScheduler} + {@link CronTrigger} 动态注册/取消任务：
 * {@code sys_job.invoke_target} 仅允许容器内实现 {@link SysTask} 接口的 bean 名称；
 * 暂停/恢复/修改 cron 均实时热更新调度容器，应用启动时由任务服务按任务表重建注册。
 * <p>
 * 并发禁止语义：同一任务上一次执行未结束时跳过本次触发并记录日志；每次执行的
 * 开始时间、耗时与结果均完整落 {@code sys_job_log}，任务体异常不向调度线程外泄。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Component
public class SysJobScheduleHolder implements ApplicationRunner {

    /**
     * 异常堆栈落库截断上限 —— 复用 Logbook 的 {@code max-body-size} 配置，
     * 与操作日志（sys_log.param/result）保持同一份截断口径
     */
    private final int errorMsgMaxLength;

    /**
     * 定时任务调度器 —— 统一由 ThreadPoolConfig 注册（sys-job- 线程前缀，规格走 thread.pool.scheduler 配置）
     */
    private final ThreadPoolTaskScheduler taskScheduler;

    /**
     * 任务注册表：容器内全部 SysTask bean（beanName → 实例），即调用目标白名单
     */
    private final Map<String, SysTask> taskBeans;

    /**
     * 任务ID → cron 调度句柄
     */
    private final Map<Long, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    /**
     * 运行中任务标记（并发禁止判定）
     */
    private final Map<Long, Boolean> runningJobs = new ConcurrentHashMap<>();

    private final SysJobLogMapper sysJobLogMapper;

    private final SysJobMapper sysJobMapper;

    public SysJobScheduleHolder(SysJobMapper sysJobMapper, SysJobLogMapper sysJobLogMapper,
                                Map<String, SysTask> taskBeans, LogbookProperties logbookProperties,
                                @Qualifier(ThreadConst.TASK_SCHEDULER_NAME) ThreadPoolTaskScheduler taskScheduler) {
        this.sysJobMapper = sysJobMapper;
        this.sysJobLogMapper = sysJobLogMapper;
        this.taskBeans = taskBeans;
        this.errorMsgMaxLength = logbookProperties.getWrite().getMaxBodySize();
        this.taskScheduler = taskScheduler;
    }

    /**
     * 应用启动后按任务表重建调度注册（内存态调度句柄不跨重启持久）。
     */
    @Override
    public void run(@NonNull ApplicationArguments args) {
        List<SysJob> jobs = sysJobMapper.selectListByQuery(
                QueryWrapper.create().where(SYS_JOB.STATUS.eq(EnableEnum.ENABLE.getCode())));
        if (CollUtil.isEmpty(jobs)) {
            log.info("无正常状态任务，无需重建调度注册");
            return;
        }
        jobs.forEach(this::register);
        log.info("定时任务调度重建完成：共注册 {} 个正常状态任务", jobs.size());
    }

    /**
     * 注册（或按新定义重建）任务的 cron 调度。
     *
     * @param job 任务实体（作为快照持有，cron 变更时先取消再注册）
     */
    public void register(SysJob job) {
        Assert.notNull(job.getId(), "任务ID不能为空");
        cancel(job.getId());
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> executeSafely(job, TriggerTypeEnum.CRON.getCode()), new CronTrigger(job.getCronExpression()));
        scheduledFutures.put(job.getId(), future);
        log.info("定时任务已注册：id={}，jobName={}，cron={}", job.getId(), job.getJobName(), job.getCronExpression());
    }

    /**
     * 取消任务的 cron 调度（无注册记录时为幂等空操作）。
     *
     * @param jobId 任务ID
     */
    public void cancel(Long jobId) {
        ScheduledFuture<?> future = scheduledFutures.remove(jobId);
        if (ObjUtil.isNotNull(future)) {
            future.cancel(false);
            log.info("定时任务已取消调度：jobId={}", jobId);
        }
    }

    /**
     * 立即手动执行一次任务（绕过 cron，异步执行，日志按手动触发记录）。
     *
     * @param job 任务实体
     */
    public void runOnce(SysJob job) {
        taskScheduler.execute(() -> executeSafely(job, TriggerTypeEnum.MANUAL.getCode()));
    }

    /**
     * 执行任务体并记录执行日志：解析调用目标 → 并发禁止检查 → 执行 → 结果落库。
     *
     * @param job         任务实体快照
     * @param triggerType 触发方式: {@link TriggerTypeEnum#CRON} 或 {@link TriggerTypeEnum#MANUAL}
     */
    private void executeSafely(SysJob job, String triggerType) {
        Long jobId = job.getId();
        // 并发禁止：上一次执行未结束时跳过本次触发（手动执行同样受约束）
        if (isConcurrentAllowed(job) && ObjUtil.isNotNull(runningJobs.putIfAbsent(jobId, Boolean.TRUE))) {
            log.warn("任务[{}]上一次执行尚未结束，本次触发已跳过", job.getJobName());
            recordLog(job, triggerType, LocalDateTime.now(), System.currentTimeMillis(),
                    SuccessEnum.FAIL.getCode(), new BusinessException("上一轮执行尚未结束，本次触发已跳过"));
            return;
        }
        LocalDateTime startTime = LocalDateTime.now();
        long beginMillis = System.currentTimeMillis();
        try {
            SysTask task = taskBeans.get(job.getInvokeTarget());
            if (ObjUtil.isNull(task)) {
                throw new BusinessException("调用目标不存在或未注册：" + job.getInvokeTarget());
            }
            task.execute();
            recordLog(job, triggerType, startTime, beginMillis, SuccessEnum.SUCCESS.getCode(), null);
        } catch (Exception e) {
            // 任务体异常完整捕获并落执行日志，不允许向调度线程外泄
            recordLog(job, triggerType, startTime, beginMillis, SuccessEnum.FAIL.getCode(), e);
            log.error("定时任务执行失败：jobId={}，jobName={}", jobId, job.getJobName(), e);
        } finally {
            if (isConcurrentAllowed(job)) {
                runningJobs.remove(jobId);
            }
        }
    }

    /**
     * 是否允许并发执行：仅显式标记「允许」时放行，缺省（null/禁止）一律跳过并发
     */
    private boolean isConcurrentAllowed(SysJob job) {
        return PermitEnum.ALLOW.getCode().equals(job.getConcurrent());
    }

    /**
     * 组装并落库一条执行日志
     */
    private void recordLog(SysJob job, String triggerType, LocalDateTime startTime, long beginMillis,
                           Integer status, Exception error) {
        String errorMsg = null;
        if (ObjUtil.isNotNull(error)) {
            // 定位根因并按截断上限落库，保留最完整的业务异常信息
            errorMsg = ExceptionUtil.stacktraceToString(ExceptionUtil.getRootCause(error), errorMsgMaxLength);
        }
        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobId(job.getId());
        jobLog.setJobName(job.getJobName());
        jobLog.setTriggerType(triggerType);
        jobLog.setStartTime(startTime);
        jobLog.setDuration(System.currentTimeMillis() - beginMillis);
        jobLog.setStatus(status);
        jobLog.setErrorMsg(errorMsg);
        sysJobLogMapper.insert(jobLog);
    }
}
