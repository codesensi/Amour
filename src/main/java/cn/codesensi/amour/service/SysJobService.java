package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.SysJobDTO;
import cn.codesensi.amour.model.dto.SysJobChangeStatusDTO;
import cn.codesensi.amour.model.dto.SysJobInsertDTO;
import cn.codesensi.amour.model.dto.SysJobLogDTO;
import cn.codesensi.amour.model.dto.SysJobLogPageDTO;
import cn.codesensi.amour.model.dto.SysJobPageDTO;
import cn.codesensi.amour.model.dto.SysJobRunDTO;
import cn.codesensi.amour.model.dto.SysJobUpdateDTO;
import com.mybatisflex.core.paginate.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务服务接口。
 * <p>
 * 承载任务定义的增删改查、启停/手动执行的热更新调度，以及执行日志的分页查询；
 * 调度容器 {@code SysJobScheduleHolder} 负责与 Spring TaskScheduler 的注册交互。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysJobService {

    /**
     * 任务分页查询（附带每个任务最近一次执行结果）。
     *
     * @param pageDTO 分页与筛选参数（任务名/分组/状态均可空）
     * @return 任务分页
     */
    Page<SysJobDTO> page(SysJobPageDTO pageDTO);

    /**
     * 新增任务：校验调用目标白名单与 cron 合法性，落库后立即注册调度。
     *
     * @param insertDTO 新增参数
     */
    void insert(SysJobInsertDTO insertDTO);

    /**
     * 修改任务：cron 热更新（先取消旧调度，按状态决定是否重新注册）。
     *
     * @param updateDTO 修改参数
     */
    void update(SysJobUpdateDTO updateDTO);

    /**
     * 启动/暂停任务：暂停取消调度句柄，恢复按当前 cron 重新注册。
     *
     * @param changeStatusDTO 启停参数
     */
    void changeStatus(SysJobChangeStatusDTO changeStatusDTO);

    /**
     * 删除任务（内置任务拒绝删除）：取消调度并跟随删除执行日志。
     *
     * @param ids 任务ID集合
     */
    void delete(List<Long> ids);

    /**
     * 立即手动执行一次任务（绕过 cron，异步执行）。
     *
     * @param runDTO 执行参数
     */
    void run(SysJobRunDTO runDTO);

    /**
     * 执行日志分页查询（jobId 可空，空时查全部任务日志）。
     *
     * @param pageDTO 分页与筛选参数
     * @return 日志分页
     */
    Page<SysJobLogDTO> logPage(SysJobLogPageDTO pageDTO);

    /**
     * 预览 cron 表达式的后续触发时间（用于新增/修改时人工校对）。
     *
     * @param cron cron 表达式
     * @return 后续触发时间列表（最多 5 次）
     */
    List<LocalDateTime> nextTriggerTimes(String cron);

}
