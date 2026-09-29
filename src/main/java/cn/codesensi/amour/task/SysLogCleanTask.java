package cn.codesensi.amour.task;

import cn.codesensi.amour.mapper.SysLogMapper;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static cn.codesensi.amour.model.entity.table.SysLogTableDef.SYS_LOG;

/**
 * 系统日志清理任务。
 * <p>
 * 物理删除超过保留期的登录与操作日志（{@code sys_log}），控制 H2 库文件长期膨胀；
 * 真正的物理 DELETE（绕过全局逻辑删除）才能回收库文件空间。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysLogCleanTask implements SysTask {

    /**
     * 日志保留天数
     */
    private static final int RETAIN_DAYS = 180;

    private final SysLogMapper sysLogMapper;

    @Override
    public void execute() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(RETAIN_DAYS);
        int deleted = LogicDeleteManager.execWithoutLogicDelete(() ->
                sysLogMapper.deleteByQuery(QueryWrapper.create()
                        .where(SYS_LOG.CREATE_TIME.lt(threshold))));
        log.info("系统日志清理完成：清理超过 {} 天的日志 {} 条", RETAIN_DAYS, deleted);
    }
}
