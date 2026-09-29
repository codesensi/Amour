package cn.codesensi.amour.task;

/**
 * 定时任务统一接口。
 * <p>
 * 所有可被定时任务调度器调度的业务任务均需实现本接口并注册为 Spring bean；
 * {@code sys_job.invoke_target} 存储实现类的 bean 名称，调度器按名称从容器解析，
 * 白名单外的目标在任务新增/修改时即被拒绝，杜绝任意方法反射调用。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysTask {

    /**
     * 执行任务。任务体应自行保证幂等与可重入；异常会被调度器捕获并记录到执行日志。
     */
    void execute();

}
