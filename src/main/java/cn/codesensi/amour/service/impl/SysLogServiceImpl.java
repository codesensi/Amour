package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.ThreadConst;
import cn.codesensi.amour.mapper.SysLogMapper;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.dto.LogPageDTO;
import cn.codesensi.amour.model.entity.SysLog;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.service.SysLogService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.codesensi.amour.model.entity.table.SysLogTableDef.SYS_LOG;

/**
 * 系统日志服务实现。
 * <p>
 * 日志实体由切面在请求线程内组装完成（traceId、用户、IP 等均取自请求上下文），
 * 本服务仅负责异步写入：入库与业务完全隔离，任何失败只降级为告警日志。
 *
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl implements SysLogService {

    private final SysLogMapper sysLogMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 异步写入操作日志。
     * <p>
     * 用户名为空且已登录时按主键轻量查询补齐（覆盖登录人信息缺失的场景）；
     * 全流程捕获异常——日志入库失败只影响日志自身，不允许反压业务。
     *
     * @param sysLog 操作日志实体
     */
    @Async(ThreadConst.LOG_EXECUTOR_NAME)
    @Override
    public void record(SysLog sysLog) {
        try {
            // 登录人用户名缺失（如公开接口未登录场景）不强制补齐；已登录时按主键补全用户名
            if (sysLog.getUserId() != null && StrUtil.isBlank(sysLog.getUsername())) {
                SysUser user = sysUserMapper.selectOneById(sysLog.getUserId());
                if (user != null) {
                    sysLog.setUsername(user.getUsername());
                }
            }
            sysLogMapper.insert(sysLog);
        } catch (Exception e) {
            log.warn("操作日志入库失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 分页查询日志（按日志类型范围过滤）。
     * <p>
     * 登录日志与操作日志同表存储、以 {@code log_type} 区分：查询范围为端点固定类型集合
     * 与用户多选条件（pageDTO.logTypes）的交集，未多选时按端点全量范围查询；
     * 用户名模糊匹配、状态精确匹配，条件缺省时自动忽略；按 ID 倒序（最新在前）。
     *
     * @param pageDTO  分页查询参数
     * @param logTypes 端点固定的日志类型范围
     * @return 日志分页结果
     */
    @Override
    public Page<SysLog> page(LogPageDTO pageDTO, List<Integer> logTypes) {
        List<Integer> scope = CollUtil.isEmpty(pageDTO.getLogTypes())
                ? logTypes
                : logTypes.stream().filter(pageDTO.getLogTypes()::contains).toList();
        if (scope.isEmpty()) {
            // 所选类型均不在本端点范围内,交集为空直接返回空页,避免空 IN 查询
            return Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize(), 0);
        }
        return QueryChain.of(sysLogMapper)
                .where(SYS_LOG.LOG_TYPE.in(scope))
                .and(SYS_LOG.USERNAME.like(pageDTO.getUsername(), StrUtil::isNotBlank))
                .and(SYS_LOG.STATUS.eq(pageDTO.getStatus(), Objects::nonNull))
                .orderBy(SYS_LOG.ID, false)
                .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }
}
