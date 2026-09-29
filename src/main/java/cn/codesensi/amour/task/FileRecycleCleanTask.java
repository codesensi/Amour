package cn.codesensi.amour.task;

import cn.codesensi.amour.common.enums.DelFlagEnum;
import cn.codesensi.amour.mapper.SysFileMapper;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.service.FileService;
import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysFileTableDef.SYS_FILE;

/**
 * 文件回收站清理任务。
 * <p>
 * 清空回收站中删除超过 30 天的文件（记录与磁盘文件一并物理删除），
 * 复用 {@link FileService#physicalDelete} 的既有物理删除语义，避免重复实现。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileRecycleCleanTask implements SysTask {

    /**
     * 回收站保留天数：超过该天数未恢复的已删除文件自动物理清理
     */
    private static final int RETAIN_DAYS = 30;

    private final SysFileMapper sysFileMapper;

    private final FileService fileService;

    @Override
    public void execute() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(RETAIN_DAYS);
        // 全局逻辑删除会对查询追加 del_flag=0 过滤，查询回收站（del_flag=1）须显式绕过
        List<SysFile> files = LogicDeleteManager.execWithoutLogicDelete(() ->
                sysFileMapper.selectListByQuery(QueryWrapper.create()
                        .where(SYS_FILE.DEL_FLAG.eq(DelFlagEnum.DELETED.getCode()))
                        .and(SYS_FILE.UPDATE_TIME.lt(threshold))));
        if (CollUtil.isEmpty(files)) {
            log.info("文件回收站清理完成：没有超过{}天的待清理文件", RETAIN_DAYS);
            return;
        }
        int success = 0;
        for (SysFile file : files) {
            try {
                fileService.physicalDelete(file.getId());
                success++;
            } catch (Exception e) {
                // 单个文件清理失败不阻塞整体，其余文件继续清理
                log.warn("文件回收站清理失败：id={}，原因：{}", file.getId(), e.getMessage());
            }
        }
        log.info("文件回收站清理完成：共 {} 个超期文件，清理成功 {} 个", files.size(), success);
    }
}
