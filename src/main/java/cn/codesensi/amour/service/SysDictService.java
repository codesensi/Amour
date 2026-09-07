package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.DictDTO;
import cn.codesensi.amour.model.dto.DictGroupDTO;
import cn.codesensi.amour.model.dto.DictPageDTO;
import cn.codesensi.amour.model.dto.DictTypeDTO;
import cn.codesensi.amour.model.entity.SysDict;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 数据字典查询服务。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysDictService {

    /**
     * 分页查询字典条目(管理端右侧数据表格的数据源,含禁用条目与完整字段)。
     * <p>
     * 编码、名称、值为模糊匹配,状态为精确匹配,条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数
     * @return 字典条目分页结果
     */
    Page<SysDict> page(DictPageDTO pageDTO);

    /**
     * 查询全部字典类型（按编码聚合,含条目数;管理端左侧类型列表的数据源）。
     *
     * @return 字典类型列表
     */
    List<DictTypeDTO> listTypes();

    /**
     * 按字典编码集合批量查询启用中的字典项分组（组内按 sort 升序）。
     * <p>逐编码复用 {@link #listByCode(String)}（优先走缓存，未命中回源查库并回填），
     * 无启用条目的编码不出现在结果中，集合中的 {@code null} 元素会被跳过。
     *
     * @param codes 字典编码集合（如 gender、enable）；为空（{@code null} 或不含元素）时返回空列表
     * @return 字典分组列表（每组含字典编码与组内条目）；无命中时返回空列表
     */
    List<DictGroupDTO> listByCodes(List<String> codes);

    /**
     * 按字典编码查询启用中的字典项列表（按 sort 升序）。
     * <p>
     * 结果按编码整组缓存（见 {@code CacheConst#DICT}），编码为空白时直接返回空列表。
     *
     * @param code 字典编码（如 gender、enable）；可为空（此时返回空列表）
     * @return 启用中的字典项列表；无命中时返回空列表
     */
    List<DictDTO> listByCode(String code);

}
