package cn.codesensi.amour.service;

import cn.codesensi.amour.model.entity.SysLog;

/**
 * 系统日志服务。
 *
 * @since 1.0
 */
public interface SysLogService {

    /**
     * 异步落库操作日志。
     * <p>
     * 由切面在请求线程内组装好完整实体后调用，异步线程仅负责写入；
     * 入库失败只记录告警，绝不影响业务主流程。
     *
     * @param sysLog 操作日志实体
     */
    void record(SysLog sysLog);

}
