package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.ThreadConst;
import cn.codesensi.amour.mapper.SysLogMapper;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.entity.SysLog;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.service.SysLogService;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}
