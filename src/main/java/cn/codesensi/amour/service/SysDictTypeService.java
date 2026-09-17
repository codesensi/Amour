package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.DictTypeDTO;
import cn.codesensi.amour.model.dto.DictTypeInsertDTO;
import cn.codesensi.amour.model.dto.DictTypeUpdateDTO;

import java.util.List;

/**
 * 数据字典类型服务。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysDictTypeService {

    /**
     * 查询全部字典类型（含组内条目数；管理端左侧类型列表的数据源）。
     * <p>
     * 按 id 升序返回，计数包含禁用条目。
     *
     * @return 字典类型列表
     */
    List<DictTypeDTO> listTypes();

    /**
     * 新增字典类型。
     * <p>
     * 校验字典编码全生命周期唯一（含逻辑删除记录）。
     *
     * @param insertDTO 字典类型信息
     */
    void insertType(DictTypeInsertDTO insertDTO);

    /**
     * 修改字典类型。
     * <p>
     * 字典编码与内置标识不可修改，仅允许维护名称与备注。
     *
     * @param updateDTO 字典类型信息
     */
    void updateType(DictTypeUpdateDTO updateDTO);

    /**
     * 批量删除字典类型。
     * <p>
     * 内置类型禁删（整批失败）；类型下存在字典条目时禁删，须先清空条目。
     *
     * @param ids 字典类型ID列表
     */
    void deleteType(List<Long> ids);

}
