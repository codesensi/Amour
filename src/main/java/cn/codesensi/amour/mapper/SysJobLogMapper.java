package cn.codesensi.amour.mapper;

import cn.codesensi.amour.model.entity.SysJobLog;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务执行日志 Mapper。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {
}
