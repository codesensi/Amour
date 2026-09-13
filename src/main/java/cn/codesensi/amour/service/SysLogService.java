package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.LogPageDTO;
import cn.codesensi.amour.model.entity.SysLog;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

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

    /**
     * 分页查询日志（按日志类型范围过滤，登录/操作日志同表存储、以日志类型区分）。
     * <p>
     * 用户名模糊匹配、状态精确匹配，条件缺省时自动忽略；结果按 ID 倒序（最新在前）。
     *
     * @param pageDTO  分页查询参数
     * @param logTypes 日志类型集合（如 登录+登出，或除登录/登出外的全部操作类型）
     * @return 日志分页结果
     */
    Page<SysLog> page(LogPageDTO pageDTO, List<Integer> logTypes);

}
