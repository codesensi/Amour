package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.enums.CacheNameEnum;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysDictDataMapper;
import cn.codesensi.amour.mapper.SysDictTypeMapper;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysDictData;
import cn.codesensi.amour.model.entity.SysDictType;
import cn.codesensi.amour.service.CacheEvictService;
import cn.codesensi.amour.service.SysDictDataService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysDictDataTableDef.SYS_DICT_DATA;
import static cn.codesensi.amour.model.entity.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * 数据字典数据服务实现。
 * <p>
 * 优先从 Caffeine 缓存（缓存名 {@code dict}，见 {@link CacheNameEnum#DICT}）以字典编码
 * （{@code dict_code}）为 Key 读取启用中的字典项列表，未命中时回源查库并回填，
 * 减少高频字典读取点的数据库压力。
 * <p>
 * 缓存采用「30 天兜底过期」策略，热更新依赖写库侧显式失效对应编码
 * （经 {@link CacheEvictService#evictDictCache(List)}）；
 * 空结果以空列表形式直接缓存，天然防止对不存在编码的反复穿透。在缓存未就绪或回源异常时
 * 降级为直接查库，保证字典读取不受缓存故障影响。
 * <p>
 * 查询结果仅含启用（status=启用）条目并按 {@code sort} 升序排列，以 {@link DictDTO} 返回。
 *
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataMapper sysDictDataMapper;
    private final SysDictTypeMapper sysDictTypeMapper;
    private final CacheManager cacheManager;
    private final DictConverter dictConverter;
    private final CacheEvictService cacheEvictService;

    /**
     * 按字典编码查询启用中的字典项列表；编码为空白时返回空列表。
     * <p>优先读取 dict 缓存，未命中时回源查库并回填，保证同一编码只回源一次。
     *
     * @param code 字典编码；可为空（此时返回空列表）
     * @return 启用中的字典项列表（按 sort 升序）；无命中时返回空列表
     */
    @Override
    public List<DictDTO> listByCode(String code) {
        if (StrUtil.isBlank(code)) {
            return List.of();
        }
        // 统一缓存读取：原子回源 + 缓存故障降级（空列表可直接缓存，防穿透）
        return CacheUtil.load(cacheManager, CacheNameEnum.DICT.getCode(), code, k -> {
            List<DictDTO> items = listByCodeDb(k);
            log.debug("dict 缓存回源查库：code={}，count={}", k, items.size());
            return items;
        });
    }

    /**
     * 按字典编码集合批量查询启用中的字典项分组；入参为空（{@code null} 或不含元素）时返回空列表。
     * <p>
     * 先逐编码命中缓存，未命中的编码合并为一条 IN 查询回源并按编码回填缓存（空结果同样缓存，防穿透），
     * 避免逐编码点查的开销；无启用条目的编码不出现在结果中，集合中的 {@code null} 元素会被跳过。
     *
     * @param codes 待查询的字典编码集合；为空时返回空列表
     * @return 字典分组列表（每组含字典编码与组内条目）；无命中时返回空列表
     */
    @Override
    public List<DictGroupDTO> listByCodes(List<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return List.of();
        }
        // 待回源编码去重（保序、跳过 null）
        List<String> distinctCodes = codes.stream().filter(ObjUtil::isNotNull).distinct().toList();
        if (CollUtil.isEmpty(distinctCodes)) {
            return List.of();
        }
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheNameEnum.DICT.getCode()));
        if (ObjUtil.isNull(cache)) {
            // 缓存未注册/未就绪：降级为一条 IN 查询直查库
            log.debug("dict 缓存未注册，降级为直接查库：codes={}", distinctCodes);
            return buildGroups(codes, listByCodesDb(distinctCodes));
        }
        // 1. 逐编码命中缓存，收集未命中的编码
        Map<String, List<DictDTO>> loaded = new LinkedHashMap<>();
        List<String> missingCodes = new ArrayList<>();
        for (String code : distinctCodes) {
            Cache.ValueWrapper wrapper = cache.get(code);
            if (ObjUtil.isNull(wrapper)) {
                missingCodes.add(code);
            } else {
                loaded.put(code, castDictList(wrapper.get()));
            }
        }
        // 2. 未命中的编码合并一条 IN 查询回源，并逐编码回填缓存（空结果同样缓存，防穿透）
        if (CollUtil.isNotEmpty(missingCodes)) {
            Map<String, List<DictDTO>> fromDb = listByCodesDb(missingCodes);
            for (String code : missingCodes) {
                List<DictDTO> items = fromDb.getOrDefault(code, List.of());
                cache.put(code, items);
                loaded.put(code, items);
            }
        }
        return buildGroups(codes, loaded);
    }

    /**
     * 还原缓存中的字典项列表（缓存层以非泛型形式存取，此处统一收口强转）。
     *
     * @param cached 缓存值
     * @return 字典项列表；缓存值为 null 时为空列表
     */
    @SuppressWarnings("unchecked")
    private List<DictDTO> castDictList(Object cached) {
        return (List<DictDTO>) ObjUtil.defaultIfNull(cached, List.of());
    }

    /**
     * 从 sys_dict_data 表按编码集合批量查询启用中的字典项（编码 -> 组内条目，组内按 sort 升序）。
     * <p>逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     *
     * @param codes 字典编码集合（去重后非空）
     * @return 编码 -> 启用中的字典项列表
     */
    private Map<String, List<DictDTO>> listByCodesDb(List<String> codes) {
        return QueryChain.of(sysDictDataMapper)
                .where(SYS_DICT_DATA.DICT_CODE.in(codes))
                .and(SYS_DICT_DATA.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .orderBy(SYS_DICT_DATA.SORT, true)
                .orderBy(SYS_DICT_DATA.ID, true)
                .list()
                .stream()
                .collect(Collectors.groupingBy(SysDictData::getDictCode, LinkedHashMap::new,
                        Collectors.mapping(dictConverter::toDTO, Collectors.toList())));
    }

    /**
     * 按入参顺序组装字典分组列表（编码为 null 或无启用条目的编码不出现）。
     *
     * @param codes  原始编码集合（保持输出顺序与入参一致）
     * @param loaded 编码 -> 字典项列表
     * @return 字典分组列表
     */
    private List<DictGroupDTO> buildGroups(List<String> codes, Map<String, List<DictDTO>> loaded) {
        List<DictGroupDTO> groups = new ArrayList<>();
        for (String code : codes) {
            if (ObjUtil.isNull(code)) {
                continue;
            }
            List<DictDTO> items = loaded.getOrDefault(code, List.of());
            if (CollUtil.isNotEmpty(items)) {
                groups.add(new DictGroupDTO().setDictCode(code).setItems(items));
            }
        }
        return groups;
    }

    /**
     * 分页查询字典条目(管理端，含禁用条目与完整字段)。
     * <p>
     * 编码、值为模糊匹配，状态为精确匹配，条件缺省时自动忽略；名称条件已归类型表，
     * 先经类型表解析为编码集合再下推（无命中编码时直接返回空页）；
     * 排序为编码升序 → 组内 sort 升序 → id 升序；页码与每页条数的缺省值由 {@code BasePage} 提供(1 与 20)。
     * <p>逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     *
     * @param pageDTO 分页查询参数
     * @return 字典条目分页结果(dictName 为自类型表回填的展示字段)
     */
    @Override
    public Page<SysDictData> dataPage(DictDataPageDTO pageDTO) {
        // 名称条件经类型表解析为编码集合（名称已归 sys_dict_type 承载）；无命中时返回空页
        List<String> nameMatchedCodes = null;
        if (StrUtil.isNotBlank(pageDTO.getDictName())) {
            nameMatchedCodes = QueryChain.of(sysDictTypeMapper)
                    .select(SYS_DICT_TYPE.DICT_CODE)
                    .where(SYS_DICT_TYPE.DICT_NAME.like(pageDTO.getDictName()))
                    .list()
                    .stream()
                    .map(SysDictType::getDictCode)
                    .toList();
            if (CollUtil.isEmpty(nameMatchedCodes)) {
                return new Page<>(List.of(), pageDTO.getPageNumber(), pageDTO.getPageSize(), 0);
            }
        }
        var query = QueryChain.of(sysDictDataMapper)
                .select(SYS_DICT_DATA.ALL_COLUMNS)
                .where(SYS_DICT_DATA.DICT_CODE.like(pageDTO.getDictCode(), StrUtil::isNotBlank))
                .and(SYS_DICT_DATA.DICT_VALUE.like(pageDTO.getDictValue(), StrUtil::isNotBlank))
                .and(SYS_DICT_DATA.STATUS.eq(pageDTO.getStatus(), ObjUtil::isNotNull));
        if (CollUtil.isNotEmpty(nameMatchedCodes)) {
            query.and(SYS_DICT_DATA.DICT_CODE.in(nameMatchedCodes));
        }
        Page<SysDictData> page = query.orderBy(SYS_DICT_DATA.DICT_CODE, true)
                .orderBy(SYS_DICT_DATA.SORT, true)
                .orderBy(SYS_DICT_DATA.ID, true)
                .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));

        // 页内编码批量回填类型名(两次轻量查询，避免 join 映射复杂度)
        fillDictNames(page);
        return page;
    }

    /**
     * 新增字典条目。
     * <p>
     * 校验字典类型存在（条目归属前置约束，类型经类型管理接口维护）；
     * 校验同编码下字典值唯一；写库后失效该编码的字典缓存。
     *
     * @param insertDTO 字典条目信息
     */
    @Override
    public void dataInsert(DictDataInsertDTO insertDTO) {
        SysDictType type = QueryChain.of(sysDictTypeMapper)
                .where(SYS_DICT_TYPE.DICT_CODE.eq(insertDTO.getDictCode()))
                .one();
        if (ObjUtil.isNull(type)) {
            throw new BusinessException("字典类型[" + insertDTO.getDictCode() + "]不存在，请先创建字典类型");
        }
        checkValueUnique(insertDTO.getDictCode(), insertDTO.getDictValue());

        SysDictData entity = dictConverter.toEntity(insertDTO);
        sysDictDataMapper.insert(entity, true);

        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(List.of(insertDTO.getDictCode())));
    }

    /**
     * 修改字典条目。
     * <p>
     * 字典编码与内置标识不可修改；状态不经本接口维护（经启停接口单独操作）；
     * 内置条目（builtin=1）锁定字典值（对齐前端 builtinLocked，后端强校验防绕过）；
     * 非内置条目修改字典值时校验同编码下唯一；写库后失效该编码的字典缓存。
     *
     * @param updateDTO 字典条目信息
     */
    @Override
    public void dataUpdate(DictDataUpdateDTO updateDTO) {
        SysDictData dictData = QueryChain.of(sysDictDataMapper)
                .where(SYS_DICT_DATA.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(dictData)) {
            throw new BusinessException("字典条目不存在");
        }

        // 内置条目锁定字典值
        if (BuiltinEnum.YES.getCode().equals(dictData.getBuiltin())
                && !dictData.getDictValue().equals(updateDTO.getDictValue())) {
            throw new BusinessException("内置字典条目不允许修改字典值");
        }

        // 字典值变化时校验同编码下唯一
        if (!dictData.getDictValue().equals(updateDTO.getDictValue())) {
            checkValueUnique(dictData.getDictCode(), updateDTO.getDictValue());
        }

        SysDictData entity = dictConverter.toEntity(updateDTO);
        sysDictDataMapper.update(entity);

        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(List.of(dictData.getDictCode())));
    }

    /**
     * 修改字典条目状态。
     * <p>
     * 内置条目（builtin=1）为系统功能依赖，不允许更改状态（始终启用）；
     * 同状态幂等返回；写库后失效该编码的字典缓存。
     *
     * @param changeStatusDTO 字典状态信息
     */
    @Override
    public void changeStatus(DictChangeStatusDTO changeStatusDTO) {
        SysDictData dictData = QueryChain.of(sysDictDataMapper)
                .where(SYS_DICT_DATA.ID.eq(changeStatusDTO.getId()))
                .one();
        if (ObjUtil.isNull(dictData)) {
            throw new BusinessException("字典条目不存在");
        }

        // 内置条目不允许更改状态
        if (BuiltinEnum.YES.getCode().equals(dictData.getBuiltin())) {
            throw new BusinessException("内置字典条目不允许更改状态");
        }

        // 状态一致时幂等返回
        if (changeStatusDTO.getStatus().equals(dictData.getStatus())) {
            return;
        }

        SysDictData entity = new SysDictData();
        entity.setId(changeStatusDTO.getId());
        entity.setStatus(changeStatusDTO.getStatus());
        sysDictDataMapper.update(entity);

        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(List.of(dictData.getDictCode())));
    }

    /**
     * 批量删除字典条目。
     * <p>
     * 内置条目不允许删除（整批失败）；删除后失效所涉编码的字典缓存，
     * 失效动作注册到事务提交后执行，避免提交前其他请求回源查库把旧值重新写入缓存。
     *
     * @param ids 字典条目ID列表
     */
    @Override
    public void dataDelete(List<Long> ids) {
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<SysDictData> dictList = QueryChain.of(sysDictDataMapper)
                .where(SYS_DICT_DATA.ID.in(distinctIds))
                .list();
        if (dictList.size() < distinctIds.size()) {
            throw new BusinessException("字典条目不存在");
        }

        // 内置条目不允许删除(整批失败)
        boolean containsBuiltin = dictList.stream()
                .anyMatch(dict -> BuiltinEnum.YES.getCode().equals(dict.getBuiltin()));
        if (containsBuiltin) {
            throw new BusinessException("内置字典条目不允许删除");
        }

        sysDictDataMapper.deleteBatchByIds(distinctIds);

        List<String> codes = dictList.stream().map(SysDictData::getDictCode).distinct().toList();
        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(codes));
    }

    /**
     * 校验同编码下字典值唯一（含已删除记录，全生命周期唯一，与唯一索引 uk_dd_code_value 口径对齐，
     * 避免服务层放行后由数据库唯一约束抛出非友好错误）。
     *
     * @param dictCode  字典编码
     * @param dictValue 字典值
     */
    private void checkValueUnique(String dictCode, String dictValue) {
        long count = LogicDeleteManager.execWithoutLogicDelete(() ->
                QueryChain.of(sysDictDataMapper)
                        .where(SYS_DICT_DATA.DICT_CODE.eq(dictCode))
                        .and(SYS_DICT_DATA.DICT_VALUE.eq(dictValue))
                        .count());
        if (count > 0) {
            throw new BusinessException("字典编码[" + dictCode + "]下字典值[" + dictValue + "]已存在");
        }
    }

    /**
     * 从 sys_dict_data 表查询指定字典编码当前启用（status=启用）的字典项，按 sort 升序。
     * <p>逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     *
     * @param code 字典编码
     * @return 启用中的字典实体列表；无命中时返回空列表
     */
    private List<DictDTO> listByCodeDb(String code) {
        List<SysDictData> dictList = QueryChain.of(sysDictDataMapper)
                .where(SYS_DICT_DATA.DICT_CODE.eq(code))
                .and(SYS_DICT_DATA.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .orderBy(SYS_DICT_DATA.SORT, true)
                .orderBy(SYS_DICT_DATA.ID, true)
                .list();
        return dictConverter.toListDTO(dictList);
    }

    /**
     * 页内编码批量回填类型名（dictName 为自 sys_dict_type 回填的展示字段）。
     *
     * @param page 字典条目分页
     */
    private void fillDictNames(Page<SysDictData> page) {
        if (page == null || CollUtil.isEmpty(page.getRecords())) {
            return;
        }
        List<String> codes = page.getRecords().stream()
                .map(SysDictData::getDictCode)
                .distinct()
                .toList();
        Map<String, String> nameMap = QueryChain.of(sysDictTypeMapper)
                .where(SYS_DICT_TYPE.DICT_CODE.in(codes))
                .list()
                .stream()
                .collect(Collectors.toMap(SysDictType::getDictCode, SysDictType::getDictName, (a, b) -> a));
        page.getRecords().forEach(record -> record.setDictName(nameMap.get(record.getDictCode())));
    }
}
