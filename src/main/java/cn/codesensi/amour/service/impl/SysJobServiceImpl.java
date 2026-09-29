package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.*;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.SysJobLogMapper;
import cn.codesensi.amour.mapper.SysJobMapper;
import cn.codesensi.amour.model.converter.SysJobConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysJob;
import cn.codesensi.amour.model.entity.SysJobLog;
import cn.codesensi.amour.service.SysJobService;
import cn.codesensi.amour.task.SysJobScheduleHolder;
import cn.codesensi.amour.task.SysTask;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.codesensi.amour.model.entity.table.SysJobLogTableDef.SYS_JOB_LOG;
import static cn.codesensi.amour.model.entity.table.SysJobTableDef.SYS_JOB;

/**
 * 定时任务服务实现。
 * <p>
 * 任务定义落库与调度容器（{@link SysJobScheduleHolder}）的注册状态始终保持同步：
 * 新增/恢复即注册，暂停/修改/删除即取消后按状态重建；调用目标仅允许容器内
 * {@link SysTask} bean（白名单校验），杜绝任意方法反射调用。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl implements SysJobService {

    /**
     * cron 触发时间预览次数
     */
    private static final int NEXT_TRIGGER_PREVIEW_COUNT = 5;

    private final SysJobMapper sysJobMapper;

    private final SysJobLogMapper sysJobLogMapper;

    private final SysJobConverter sysJobConverter;

    private final SysJobScheduleHolder scheduleHolder;

    /**
     * 容器内全部 SysTask bean（beanName → 实例），作为调用目标白名单
     */
    private final Map<String, SysTask> taskBeans;

    @Override
    public Page<SysJobDTO> page(SysJobPageDTO pageDTO) {
        Page<SysJob> page = QueryChain.of(sysJobMapper)
                .where(SYS_JOB.JOB_NAME.like(pageDTO.getJobName(), StrUtil.isNotBlank(pageDTO.getJobName())))
                .and(SYS_JOB.JOB_GROUP.eq(pageDTO.getJobGroup(), ObjUtil.isNotNull(pageDTO.getJobGroup())))
                .and(SYS_JOB.STATUS.eq(pageDTO.getStatus(), ObjUtil.isNotNull(pageDTO.getStatus())))
                .orderBy(SYS_JOB.CREATE_TIME, false)
                .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
        Page<SysJobDTO> dtoPage = sysJobConverter.toPageDTO(page);
        fillLastResult(dtoPage);
        return dtoPage;
    }

    @Override
    public void insert(SysJobInsertDTO insertDTO) {
        validateInvokeTarget(insertDTO.getInvokeTarget());
        validateInvokeTargetUnique(insertDTO.getInvokeTarget());
        parseCron(insertDTO.getCronExpression());
        SysJob job = sysJobConverter.toEntity(insertDTO);
        job.setJobGroup(normalizeJobGroup(insertDTO.getJobGroup()));
        job.setConcurrent(normalizeConcurrent(insertDTO.getConcurrent()));
        job.setStatus(EnableEnum.ENABLE.getCode());
        job.setBuiltin(BuiltinEnum.NO.getCode());
        sysJobMapper.insert(job);
        scheduleHolder.register(job);
    }

    @Override
    public void update(SysJobUpdateDTO updateDTO) {
        SysJob job = requireJob(Long.valueOf(updateDTO.getId()));
        parseCron(updateDTO.getCronExpression());
        // 调用目标是任务身份，不在修改契约内；分组仅内置任务为部署期约定，非内置可重新归类
        if (!BuiltinEnum.YES.getCode().equals(job.getBuiltin())) {
            job.setJobGroup(normalizeJobGroup(updateDTO.getJobGroup()));
        }
        job.setJobName(updateDTO.getJobName());
        job.setCronExpression(updateDTO.getCronExpression());
        job.setConcurrent(normalizeConcurrent(updateDTO.getConcurrent()));
        job.setRemark(updateDTO.getRemark());
        sysJobMapper.update(job);
        // 暂停状态下修改定义不启动调度，恢复时再按新定义注册
        if (EnableEnum.ENABLE.getCode().equals(job.getStatus())) {
            scheduleHolder.register(job);
        } else {
            scheduleHolder.cancel(job.getId());
        }
    }

    @Override
    public void changeStatus(SysJobChangeStatusDTO changeStatusDTO) {
        SysJob job = requireJob(Long.valueOf(changeStatusDTO.getId()));
        Integer targetStatus = changeStatusDTO.getStatus();
        if (targetStatus.equals(job.getStatus())) {
            return;
        }
        job.setStatus(targetStatus);
        sysJobMapper.update(job);
        if (EnableEnum.ENABLE.getCode().equals(targetStatus)) {
            scheduleHolder.register(job);
        } else {
            scheduleHolder.cancel(job.getId());
        }
    }

    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<SysJob> jobs = sysJobMapper.selectListByIds(ids);
        if (CollUtil.isEmpty(jobs)) {
            return;
        }
        boolean containsBuiltin = jobs.stream().anyMatch(job -> BuiltinEnum.YES.getCode().equals(job.getBuiltin()));
        if (containsBuiltin) {
            throw new BusinessException("内置任务不允许删除");
        }
        // 执行日志跟随任务彻底清除：任务已删则日志失去关联价值，走物理删除避免回收站语义
        LogicDeleteManager.execWithoutLogicDelete(() ->
                sysJobLogMapper.deleteByQuery(QueryWrapper.create().where(SYS_JOB_LOG.JOB_ID.in(ids))));
        sysJobMapper.deleteBatchByIds(ids);
        ids.forEach(scheduleHolder::cancel);
    }

    @Override
    public void run(SysJobRunDTO runDTO) {
        SysJob job = requireJob(Long.valueOf(runDTO.getId()));
        scheduleHolder.runOnce(job);
    }

    @Override
    public Page<SysJobLogDTO> logPage(SysJobLogPageDTO pageDTO) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_JOB_LOG.JOB_ID.eq(pageDTO.getJobId(), ObjUtil.isNotNull(pageDTO.getJobId())))
                .and(SYS_JOB_LOG.STATUS.eq(pageDTO.getStatus(), ObjUtil.isNotNull(pageDTO.getStatus())))
                .orderBy(SYS_JOB_LOG.START_TIME, false);
        Page<SysJobLog> page = sysJobLogMapper.paginate(
                Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()), queryWrapper);
        return sysJobConverter.toLogPageDTO(page);
    }

    @Override
    public List<LocalDateTime> nextTriggerTimes(String cron) {
        CronExpression expression = parseCron(cron);
        List<LocalDateTime> result = new ArrayList<>(NEXT_TRIGGER_PREVIEW_COUNT);
        LocalDateTime current = LocalDateTime.now();
        for (int i = 0; i < NEXT_TRIGGER_PREVIEW_COUNT; i++) {
            current = expression.next(current);
            if (ObjUtil.isNull(current)) {
                break;
            }
            result.add(current);
        }
        return result;
    }

    /**
     * 任务必须存在，否则抛出业务异常
     */
    private SysJob requireJob(Long id) {
        SysJob job = sysJobMapper.selectOneById(id);
        if (ObjUtil.isNull(job)) {
            throw new BusinessException("任务不存在或已被删除");
        }
        return job;
    }

    /**
     * 调用目标白名单校验：仅允许容器内注册的 SysTask bean
     */
    private void validateInvokeTarget(String invokeTarget) {
        if (!taskBeans.containsKey(invokeTarget)) {
            throw new BusinessException("调用目标不存在，可选项：" + String.join("、", taskBeans.keySet()));
        }
    }

    /**
     * 调用目标全生命周期唯一性查重（含逻辑删除记录，对齐唯一索引 uk_j_invoke_target）。
     * 仅新增时调用——调用目标不在修改契约内，不存在与自身排除的场景。
     *
     * @param invokeTarget 调用目标
     */
    private void validateInvokeTargetUnique(String invokeTarget) {
        long count = LogicDeleteManager.execWithoutLogicDelete(() -> sysJobMapper
                .selectCountByQuery(QueryWrapper.create()
                        .where(SYS_JOB.INVOKE_TARGET.eq(invokeTarget))));
        if (count > 0) {
            throw new BusinessException("调用目标已存在任务：" + invokeTarget);
        }
    }

    /**
     * 解析 cron 表达式，非法时抛出业务异常（兼具合法性校验作用）
     */
    private CronExpression parseCron(String cron) {
        try {
            return CronExpression.parse(cron);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("cron 表达式不合法：" + cron);
        }
    }

    /**
     * 并发开关归一化：仅「允许」视为允许并发，其余一律按禁止处理
     */
    private Integer normalizeConcurrent(Integer concurrent) {
        return PermitEnum.ALLOW.getCode().equals(concurrent)
                ? PermitEnum.ALLOW.getCode() : PermitEnum.FORBID.getCode();
    }

    /**
     * 任务分组归一化：缺省按「默认」分组处理，非 JobGroupEnum 定义的编码直接拒绝
     */
    private String normalizeJobGroup(String jobGroup) {
        if (ObjUtil.isNull(jobGroup)) {
            return JobGroupEnum.DEFAULT.getCode();
        }
        if (ObjUtil.isNull(BaseEnum.fromCode(JobGroupEnum.class, jobGroup))) {
            throw new BusinessException("任务分组不合法，可选项：" + JobGroupEnum.DEFAULT.getCode()
                    + "/" + JobGroupEnum.INFRA.getCode());
        }
        return jobGroup;
    }

    /**
     * 为分页中的每个任务填充最近一次执行结果（任务数量个位数，逐条查询成本可忽略）
     */
    private void fillLastResult(Page<SysJobDTO> dtoPage) {
        if (ObjUtil.isNull(dtoPage) || CollUtil.isEmpty(dtoPage.getRecords())) {
            return;
        }
        for (SysJobDTO jobDTO : dtoPage.getRecords()) {
            SysJobLog lastLog = sysJobLogMapper.selectOneByQuery(QueryWrapper.create()
                    .where(SYS_JOB_LOG.JOB_ID.eq(jobDTO.getId()))
                    .orderBy(SYS_JOB_LOG.START_TIME, false)
                    .limit(1));
            if (ObjUtil.isNull(lastLog)) {
                continue;
            }
            jobDTO.setLastStatus(lastLog.getStatus());
            jobDTO.setLastDuration(lastLog.getDuration());
            jobDTO.setLastStartTime(lastLog.getStartTime());
        }
    }
}
