package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.model.converter.ConfigConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.mapper.SysConfigMapper;
import cn.codesensi.amour.service.ConfigService;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysConfigTableDef.SYS_CONFIG;

/**
 * 运行时配置查询服务实现。
 * <p>
 * 优先从 Caffeine 缓存（配置名 {@code config}，见 {@link CacheConst#CONFIG}）读取以点分路径
 * （如 {@code name}、{@code captcha.sms-expire}）作为 {@code config_key} 存储的配置，未命中时回源查库并回填，
 * 减少高频配置点的数据库压力。
 * <p>
 * 缓存采用"驻留不自动过期"策略，热更新依赖写库侧显式调用 {@link #evictCache(List)}
 * 失效对应配置键；在缓存未就绪或回源异常时降级为直接查库，保证配置读取不受缓存故障影响。
 * <p>
 * 查询结果以 {@link ConfigDTO} 返回；当配置键在库中不存在或处于停用状态时，
 * 结果中不包含对应条目，避免调用侧因缺配置而失败。
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    /**
     * 缓存空值的哨兵对象：Caffeine 不允许缓存 {@code null}，用该哨兵占位，读取时再还原为 {@code null}，
     * 从而让"配置不存在"的结果也能被缓存，避免反复回源查库。
     */
    private static final Object NULL_MARKER = new Object();

    private final SysConfigMapper sysConfigMapper;
    private final CacheManager cacheManager;
    private final ConfigConverter configConverter;

    /**
     * 按配置键集合批量查询配置；入参为空（{@code null} 或不含元素）时返回全部启用的配置。
     * <p>
     * 指定 keys 时逐个按键读取（优先走缓存，未命中回源查库并回填），
     * 不存在或停用的配置键不出现在结果中，集合中的 {@code null} 元素会被跳过。
     *
     * @param keys 待查询的配置键集合（app 之下的点分路径）；为空时查询全部
     * @return 配置 DTO 列表；无命中时返回空列表
     */
    @Override
    public List<ConfigDTO> listByKeys(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return listAllFromDb();
        }
        List<ConfigDTO> result = new ArrayList<>();
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            SysConfig config = oneConfigByKey(key);
            if (config != null) {
                result.add(configConverter.toDTO(config));
            }
        }
        return result;
    }

    /**
     * 失效配置缓存：入参为空（{@code null} 或不含元素）时清空整个缓存，否则逐个失效对应配置键。
     * <p>集合中的 {@code null} 元素会被跳过，避免缓存层对空键抛出异常。
     *
     * @param keys 待失效的配置键集合；为空时清除全部
     */
    @Override
    public void evictCache(List<String> keys) {
        Cache cache = configCache();
        if (cache == null) {
            return;
        }
        if (keys == null || keys.isEmpty()) {
            cache.clear();
            return;
        }
        for (String key : keys) {
            if (key != null) {
                cache.evict(key);
            }
        }
    }

    /**
     * 从 config 缓存读取指定配置键当前启用（status=启用）的配置；未命中时回源查库并回填缓存。
     *
     * @param key 配置键
     * @return 启用中的配置实体；不存在或停用返回 {@code null}
     */
    private SysConfig oneConfigByKey(String key) {
        Cache cache = configCache();
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            return oneConfigByKeyFromDb(key);
        }
        try {
            // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿
            Object cached = cache.get(key, () -> loadFromDb(key));
            return cached == NULL_MARKER ? null : (SysConfig) cached;
        } catch (Cache.ValueRetrievalException e) {
            // 回源异常时降级为直接查库，避免缓存故障阻断配置读取
            return oneConfigByKeyFromDb(key);
        }
    }

    /**
     * 配置缓存回源加载器：查库一次并回填；未命中（不存在/停用）以 {@link #NULL_MARKER} 哨兵占位。
     *
     * @param key 配置键
     * @return 配置实体或空值哨兵
     */
    private Object loadFromDb(String key) {
        SysConfig config = oneConfigByKeyFromDb(key);
        return config == null ? NULL_MARKER : config;
    }

    /**
     * 获取 config 缓存实例；未注册该缓存时返回 {@code null}。
     *
     * @return config 缓存，或 {@code null}
     */
    private Cache configCache() {
        return cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.CONFIG));
    }

    /**
     * 从 sys_config 表查询指定配置键当前启用（status=启用）的配置记录。
     *
     * @param key 配置键
     * @return 启用中的配置实体；不存在或停用返回 {@code null}
     */
    private SysConfig oneConfigByKeyFromDb(String key) {
        return QueryChain.of(sysConfigMapper)
                .where(SYS_CONFIG.C_KEY.eq(key))
                .and(SYS_CONFIG.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .one();
    }

    /**
     * 查询全部启用（status=启用）的配置。
     * <p>缓存就绪时逐条回填缓存，顺带完成常用配置点的预热。
     *
     * @return 配置 DTO 列表；无数据时返回空列表
     */
    private List<ConfigDTO> listAllFromDb() {
        List<SysConfig> configs = QueryChain.of(sysConfigMapper)
                .where(SYS_CONFIG.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .list();
        Cache cache = configCache();
        // 缓存就绪时逐条回填缓存，顺带完成常用配置点的预热
        if (cache != null) {
            for (SysConfig config : configs) {
                cache.put(config.getCKey(), config);
            }
        }
        return configConverter.toDTOList(configs);
    }
}
