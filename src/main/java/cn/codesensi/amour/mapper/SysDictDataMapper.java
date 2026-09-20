package cn.codesensi.amour.mapper;

import cn.codesensi.amour.model.dto.DictCodeCountDTO;
import cn.codesensi.amour.model.entity.SysDictData;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据字典数据 Mapper。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 按字典编码分组统计条目数（含禁用条目）。
     * <p>字典条目为物理删除，表内恒为有效数据，无需逻辑删除过滤。
     *
     * @return 每个字典编码一行的计数结果
     */
    @Select("SELECT dict_code AS dictCode, COUNT(*) AS count FROM sys_dict_data GROUP BY dict_code")
    List<DictCodeCountDTO> selectCountByDictCode();

}
