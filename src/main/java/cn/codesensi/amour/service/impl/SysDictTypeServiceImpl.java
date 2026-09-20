package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.SysDictDataMapper;
import cn.codesensi.amour.mapper.SysDictTypeMapper;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.DictCodeCountDTO;
import cn.codesensi.amour.model.dto.DictTypeDTO;
import cn.codesensi.amour.model.dto.DictTypeInsertDTO;
import cn.codesensi.amour.model.dto.DictTypeUpdateDTO;
import cn.codesensi.amour.model.entity.SysDictType;
import cn.codesensi.amour.service.SysDictTypeService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysDictDataTableDef.SYS_DICT_DATA;
import static cn.codesensi.amour.model.entity.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * 数据字典类型服务实现。
 * <p>
 * 类型为字典编码的归属行，承载类型名与类型级元数据；条目计数、重名等聚合视图
 * 均由本服务组装。类型不参与缓存（仅条目按编码整组缓存），读频率远低于条目下发。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataMapper sysDictDataMapper;
    private final DictConverter dictConverter;

    /**
     * 查询全部字典类型（含组内条目数；管理端左侧类型列表的数据源）。
     * <p>类型表全量按 id 升序加载，条目计数由数据表按编码 GROUP BY 聚合下推数据库，
     * 计数包含禁用条目（对齐历史口径）；逻辑删除（del_flag）由全局配置自动过滤。
     *
     * @return 字典类型列表
     */
    @Override
    public List<DictTypeDTO> listTypes() {
        List<SysDictType> types = QueryChain.of(sysDictTypeMapper)
                .orderBy(SYS_DICT_TYPE.ID, true)
                .list();
        if (CollUtil.isEmpty(types)) {
            return List.of();
        }
        Map<String, Long> countMap = sysDictDataMapper.selectCountByDictCode()
                .stream()
                .collect(Collectors.toMap(DictCodeCountDTO::getDictCode, DictCodeCountDTO::getCount));
        return types.stream()
                .map(type -> new DictTypeDTO()
                        .setId(type.getId())
                        .setDictCode(type.getDictCode())
                        .setDictName(type.getDictName())
                        .setBuiltin(type.getBuiltin())
                        .setCount(countMap.getOrDefault(type.getDictCode(), 0L).intValue())
                        .setRemark(type.getRemark()))
                .toList();
    }

    /**
     * 新增字典类型。
     * <p>
     * 校验字典编码全生命周期唯一（含逻辑删除记录，与唯一索引 uk_dt_code 口径对齐，
     * 避免服务层放行后由数据库唯一约束抛出非友好错误）。
     *
     * @param insertDTO 字典类型信息
     */
    @Override
    public void insertType(DictTypeInsertDTO insertDTO) {
        String dictCode = insertDTO.getDictCode();
        long count = LogicDeleteManager.execWithoutLogicDelete(() ->
                QueryChain.of(sysDictTypeMapper)
                        .where(SYS_DICT_TYPE.DICT_CODE.eq(dictCode))
                        .count());
        if (count > 0) {
            throw new BusinessException("字典编码[" + dictCode + "]已存在");
        }
        SysDictType entity = dictConverter.toEntity(insertDTO);
        sysDictTypeMapper.insert(entity, true);
    }

    /**
     * 修改字典类型。
     * <p>
     * 字典编码与内置标识不可修改（不在可提交字段之列）；仅允许维护名称与备注；
     * 类型名不进入条目缓存（条目缓存不含名称字段），无需失效字典缓存。
     *
     * @param updateDTO 字典类型信息
     */
    @Override
    public void updateType(DictTypeUpdateDTO updateDTO) {
        SysDictType type = QueryChain.of(sysDictTypeMapper)
                .where(SYS_DICT_TYPE.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(type)) {
            throw new BusinessException("字典类型不存在");
        }
        // 经 UpdateChain 显式逐列赋值，备注置空时写入 null（库中不落空串）
        UpdateChain.of(SysDictType.class)
                .set(SYS_DICT_TYPE.DICT_NAME, updateDTO.getDictName())
                .set(SYS_DICT_TYPE.REMARK, updateDTO.getRemark())
                .where(SYS_DICT_TYPE.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 批量删除字典类型。
     * <p>
     * 内置类型不允许删除（整批失败）；类型下存在字典条目时禁删，须先清空条目。
     *
     * @param ids 字典类型ID列表
     */
    @Override
    public void deleteType(List<Long> ids) {
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<SysDictType> types = QueryChain.of(sysDictTypeMapper)
                .where(SYS_DICT_TYPE.ID.in(distinctIds))
                .list();
        if (types.size() < distinctIds.size()) {
            throw new BusinessException("字典类型不存在");
        }

        // 内置类型不允许删除（整批失败）
        boolean containsBuiltin = types.stream()
                .anyMatch(type -> BuiltinEnum.YES.getCode().equals(type.getBuiltin()));
        if (containsBuiltin) {
            throw new BusinessException("内置字典类型不允许删除");
        }

        // 类型下存在字典条目时禁删（删除类型不级联删除条目）
        List<String> codes = types.stream().map(SysDictType::getDictCode).distinct().toList();
        long itemCount = QueryChain.of(sysDictDataMapper)
                .where(SYS_DICT_DATA.DICT_CODE.in(codes))
                .count();
        if (itemCount > 0) {
            throw new BusinessException("字典类型下存在字典条目，请先删除条目");
        }

        sysDictTypeMapper.deleteBatchByIds(distinctIds);
    }

}
