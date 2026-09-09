package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.BuiltinEnum;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysDictMapper;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysDict;
import cn.codesensi.amour.service.CacheEvictService;
import cn.codesensi.amour.service.SysDictService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
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

import static cn.codesensi.amour.model.entity.table.SysDictTableDef.SYS_DICT;

/**
 * 数据字典查询服务实现。
 * <p>
 * 优先从 Caffeine 缓存（缓存名 {@code dict}，见 {@link CacheConst#DICT}）以字典编码
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
public class SysDictServiceImpl implements SysDictService {

    private final SysDictMapper sysDictMapper;
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
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.DICT));
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            log.debug("dict 缓存未注册，降级为直接查库：code={}", code);
            return listByCodeDb(code);
        }
        try {
            // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿；空列表可直接缓存（防穿透）
            return cache.get(code, () -> {
                List<DictDTO> items = listByCodeDb(code);
                log.debug("dict 缓存回源查库：code={}，count={}", code, items.size());
                return items;
            });
        } catch (Cache.ValueRetrievalException e) {
            // 回源异常时降级为直接查库，避免缓存故障阻断字典读取
            log.debug("dict 缓存回源异常，降级为直接查库：code={}", code, e);
            return listByCodeDb(code);
        }
    }

    /**
     * 按字典编码集合批量查询启用中的字典项分组；入参为空（{@code null} 或不含元素）时返回空列表。
     * <p>逐编码复用 {@link #listByCode(String)}（优先走缓存，未命中回源查库并回填），
     * 无启用条目的编码不出现在结果中，集合中的 {@code null} 元素会被跳过。
     *
     * @param codes 待查询的字典编码集合；为空时返回空列表
     * @return 字典分组列表（每组含字典编码与组内条目）；无命中时返回空列表
     */
    @Override
    public List<DictGroupDTO> listByCodes(List<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return List.of();
        }
        List<DictGroupDTO> groups = new ArrayList<>();
        for (String code : codes) {
            if (code == null) {
                continue;
            }
            List<DictDTO> items = listByCode(code);
            if (!items.isEmpty()) {
                groups.add(new DictGroupDTO().setDictCode(code).setItems(items));
            }
        }
        return groups;
    }

    /**
     * 查询全部字典类型(按编码聚合，含条目数；管理端左侧类型列表的数据源)。
     * <p>字典为单表扁平结构，"类型"是由 {@code dict_code} 聚合出的分组视角:
     * 全表按 id 升序加载后内存聚合，名称取组内首行，计数包含禁用条目。
     *
     * @return 字典类型列表
     */
    @Override
    public List<DictTypeDTO> listTypes() {
        List<SysDict> dictList = QueryChain.of(sysDictMapper)
                .orderBy(SYS_DICT.ID, true)
                .list();
        Map<String, DictTypeDTO> types = new LinkedHashMap<>();
        for (SysDict dict : dictList) {
            DictTypeDTO type = types.computeIfAbsent(dict.getDictCode(),
                    code -> new DictTypeDTO().setDictCode(code).setCount(0));
            type.setDictName(dict.getDictName());
            type.setCount(type.getCount() + 1);
        }
        return new ArrayList<>(types.values());
    }

    /**
     * 分页查询字典条目(管理端，含禁用条目与完整字段)。
     * <p>
     * 编码、名称、值为模糊匹配，状态为精确匹配，条件缺省时自动忽略；
     * 排序为编码升序 → 组内 sort 升序 → id 升序；页码与每页条数的缺省值由 {@link BasePage} 提供(1 与 20)。
     * <p>逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     *
     * @param pageDTO 分页查询参数
     * @return 字典条目分页结果
     */
    @Override
    public Page<SysDict> page(DictPageDTO pageDTO) {
        return QueryChain.of(sysDictMapper)
                .select(SYS_DICT.ALL_COLUMNS)
                .where(SYS_DICT.DICT_CODE.like(pageDTO.getDictCode(), StrUtil::isNotBlank))
                .and(SYS_DICT.DICT_NAME.like(pageDTO.getDictName(), StrUtil::isNotBlank))
                .and(SYS_DICT.DICT_VALUE.like(pageDTO.getDictValue(), StrUtil::isNotBlank))
                .and(SYS_DICT.STATUS.eq(pageDTO.getStatus(), ObjUtil::isNotNull))
                .orderBy(SYS_DICT.DICT_CODE, true)
                .orderBy(SYS_DICT.SORT, true)
                .orderBy(SYS_DICT.ID, true)
                .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    /**
     * 新增字典条目。
     * <p>
     * 校验同编码下字典值唯一；写库后失效该编码的字典缓存。
     *
     * @param insertDTO 字典条目信息
     */
    @Override
    public void insert(DictInsertDTO insertDTO) {
        checkValueUnique(insertDTO.getDictCode(), insertDTO.getDictValue());

        SysDict sysDict = dictConverter.toEntity(insertDTO);
        sysDictMapper.insert(sysDict, true);

        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(List.of(insertDTO.getDictCode())));
    }

    /**
     * 修改字典条目。
     * <p>
     * 字典编码与内置标识不可修改；内置条目（builtin=1）锁定字典值（对齐前端 builtinLocked，
     * 后端强校验防绕过）；非内置条目修改字典值时校验同编码下唯一；写库后失效该编码的字典缓存。
     *
     * @param updateDTO 字典条目信息
     */
    @Override
    public void update(DictUpdateDTO updateDTO) {
        SysDict sysDict = QueryChain.of(sysDictMapper)
                .where(SYS_DICT.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(sysDict)) {
            throw new BusinessException("字典条目不存在");
        }

        // 内置条目锁定字典值
        if (BuiltinEnum.YES.getCode().equals(sysDict.getBuiltin())
                && !sysDict.getDictValue().equals(updateDTO.getDictValue())) {
            throw new BusinessException("内置字典条目不允许修改字典值");
        }

        // 字典值变化时校验同编码下唯一
        if (!sysDict.getDictValue().equals(updateDTO.getDictValue())) {
            checkValueUnique(sysDict.getDictCode(), updateDTO.getDictValue());
        }

        SysDict entity = dictConverter.toEntity(updateDTO);
        sysDictMapper.update(entity);

        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(List.of(sysDict.getDictCode())));
    }

    /**
     * 修改字典条目状态。
     * <p>
     * 内置条目仅承载展示层，允许启停；同状态幂等返回；写库后失效该编码的字典缓存。
     *
     * @param changeStatusDTO 字典状态信息
     */
    @Override
    public void changeStatus(DictChangeStatusDTO changeStatusDTO) {
        SysDict sysDict = QueryChain.of(sysDictMapper)
                .where(SYS_DICT.ID.eq(changeStatusDTO.getId()))
                .one();
        if (ObjUtil.isNull(sysDict)) {
            throw new BusinessException("字典条目不存在");
        }

        // 状态一致时幂等返回
        if (changeStatusDTO.getStatus().equals(sysDict.getStatus())) {
            return;
        }

        SysDict entity = new SysDict();
        entity.setId(changeStatusDTO.getId());
        entity.setStatus(changeStatusDTO.getStatus());
        sysDictMapper.update(entity);

        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(List.of(sysDict.getDictCode())));
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
    public void delete(List<Long> ids) {
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<SysDict> dictList = QueryChain.of(sysDictMapper)
                .where(SYS_DICT.ID.in(distinctIds))
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

        sysDictMapper.deleteBatchByIds(distinctIds);

        List<String> codes = dictList.stream().map(SysDict::getDictCode).distinct().toList();
        CacheUtil.evictAfterCommit(() -> cacheEvictService.evictDictCache(codes));
    }

    /**
     * 校验同编码下字典值唯一（逻辑删除的行由全局配置自动排除）。
     *
     * @param dictCode  字典编码
     * @param dictValue 字典值
     */
    private void checkValueUnique(String dictCode, String dictValue) {
        long count = QueryChain.of(sysDictMapper)
                .where(SYS_DICT.DICT_CODE.eq(dictCode))
                .and(SYS_DICT.DICT_VALUE.eq(dictValue))
                .count();
        if (count > 0) {
            throw new BusinessException("字典编码[" + dictCode + "]下字典值[" + dictValue + "]已存在");
        }
    }

    /**
     * 从 sys_dict 表查询指定字典编码当前启用（status=启用）的字典项，按 sort 升序。
     * <p>逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     *
     * @param code 字典编码
     * @return 启用中的字典实体列表；无命中时返回空列表
     */
    private List<DictDTO> listByCodeDb(String code) {
        List<SysDict> dictList = QueryChain.of(sysDictMapper)
                .where(SYS_DICT.DICT_CODE.eq(code))
                .and(SYS_DICT.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .orderBy(SYS_DICT.SORT, true)
                .orderBy(SYS_DICT.ID, true)
                .list();
        return dictConverter.toListDTO(dictList);
    }

}