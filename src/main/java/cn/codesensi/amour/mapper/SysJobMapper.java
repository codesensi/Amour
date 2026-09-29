package cn.codesensi.amour.mapper;

import cn.codesensi.amour.model.entity.SysJob;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 Mapper。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {
}
