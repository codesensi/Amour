package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysDictMapper;
import cn.codesensi.amour.model.converter.DictConverter;
import cn.codesensi.amour.model.dto.DictDTO;
import cn.codesensi.amour.model.dto.DictGroupDTO;
import cn.codesensi.amour.model.entity.SysDict;
import cn.codesensi.amour.service.SysDictService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysDictTableDef.SYS_DICT;

/**
 * 数据字典查询服务实现。
 * <p>
 * 优先从 Caffeine 缓存（缓存名 {@code dict}，见 {@link CacheConst#DICT}）以字典编码
 * （{@code dict_code}）为 Key 读取启用中的字典项列表，未命中时回源查库并回填，
 * 减少高频字典读取点的数据库压力。
 * <p>
 * 缓存采用「30 天兜底过期」策略，热更新依赖写库侧显式失效对应编码（随字典管理接口落地时补充）；
 * 空结果以空列表形式直接缓存，天然防止对不存在编码的反复穿透。在缓存未就绪或回源异常时
 * 降级为直接查库，保证字典读取不受缓存故障影响。
 * <p>
 * 查询结果仅含启用（status=启用）条目并按 {@code sort} 升序排列，以 {@link DictDTO} 返回。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl implements SysDictService {

    private final SysDictMapper sysDictMapper;
    private final CacheManager cacheManager;
    private final DictConverter dictConverter;

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
        Cache cache = dictCache();
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
     * 获取 dict 缓存实例；未注册该缓存时返回 {@code null}。
     *
     * @return dict 缓存，或 {@code null}
     */
    private Cache dictCache() {
        return cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.DICT));
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
