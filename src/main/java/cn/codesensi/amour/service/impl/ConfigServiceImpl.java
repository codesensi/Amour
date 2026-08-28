package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.entity.SysConfig;
import cn.codesensi.amour.mapper.SysConfigMapper;
import cn.codesensi.amour.service.ConfigService;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.entity.table.SysConfigTableDef.SYS_CONFIG;

/**
 * 运行时配置查询服务实现。
 * <p>
 * 优先从 Caffeine 缓存（配置名 {@code config}，见 {@link CacheConst#CONFIG}）读取以 point 路径
 * （如 {@code name}、{@code captcha.sms-expire}）作为 {@code config_key} 存储的配置，未命中时回源查库并回填，
 * 减少高频配置点的数据库压力。
 * <p>
 * 缓存采用"驻留不自动过期"策略，热更新依赖写库侧显式调用 {@link #evictCache(List)}
 * 失效对应配置键；在缓存未就绪或回源异常时降级为直接查库，保证配置读取不受缓存故障影响。
 * <p>
 * 当配置键在库中不存在或处于停用状态时，各方法回落到对应类型的默认值，避免调用侧因缺配置而失败：
 * <ul>
 *   <li>{@link #getString(String)} → {@code null}</li>
 *   <li>{@link #getBool(String)} → {@code false}</li>
 *   <li>{@link #getInt(String)} → {@code 0}</li>
 * </ul>
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

    /**
     * 按键读取字符串配置。
     * <p>配置缺失或停用时返回 {@code null}。
     *
     * @param key 配置键（app 之下的点分路径，如 {@code name}）
     * @return 配置值字符串；不存在/停用返回 {@code null}
     */
    @Override
    public String getString(String key) {
        return raw(key);
    }

    /**
     * 按键读取布尔配置。
     * <p>配置缺失或停用，或取回的字符串无法解析为布尔时返回 {@code false}。
     *
     * @param key 配置键（app 之下的点分路径，如 {@code demo-mode}）
     * @return 布尔配置值；不存在/停用返回 {@code false}
     */
    @Override
    public boolean getBool(String key) {
        return Boolean.parseBoolean(raw(key));
    }

    /**
     * 按键读取整数配置。
     * <p>配置缺失或停用时返回 {@code 0}（对 null 安全，不会抛出解析异常）。
     *
     * @param key 配置键（app 之下的点分路径，如 {@code captcha.image-expire}）
     * @return 整数配置值；不存在/停用返回 {@code 0}
     */
    @Override
    public int getInt(String key) {
        String value = raw(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    /**
     * 按键读取长整数配置。
     * <p>配置缺失或停用时返回 {@code 0L}（对 null 安全，不会抛出解析异常）。
     *
     * @param key 配置键（app 之下的点分路径，如 {@code captcha.image-expire}）
     * @return 长整数配置值；不存在/停用返回 {@code 0L}
     */
    @Override
    public long getLong(String key) {
        String value = raw(key);
        return value == null ? 0L : Long.parseLong(value);
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
     * 从 config 缓存读取指定配置键当前启用（status=启用）的配置值；未命中时回源查库并回填缓存。
     *
     * @param key 配置键
     * @return 配置值字符串；不存在或停用返回 {@code null}
     */
    private String raw(String key) {
        Cache cache = configCache();
        if (cache == null) {
            // 缓存未注册/未就绪：降级为直接查库
            return queryDb(key);
        }
        try {
            // 原子回源：未命中时执行 loader 查库并写入，防止缓存击穿
            Object cached = cache.get(key, () -> loadFromDb(key));
            return cached == NULL_MARKER ? null : (String) cached;
        } catch (Cache.ValueRetrievalException e) {
            // 回源异常时降级为直接查库，避免缓存故障阻断配置读取
            return queryDb(key);
        }
    }

    /**
     * 配置缓存回源加载器：查库一次并回填；未命中（不存在/停用）以 {@link #NULL_MARKER} 哨兵占位。
     *
     * @param key 配置键
     * @return 配置值字符串或空值哨兵
     */
    private Object loadFromDb(String key) {
        String value = queryDb(key);
        return value == null ? NULL_MARKER : value;
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
     * 从 sys_config 表查询指定配置键当前启用（status=启用）的配置值。
     *
     * @param key 配置键
     * @return 配置值字符串；不存在或停用返回 {@code null}
     */
    private String queryDb(String key) {
        SysConfig c = QueryChain.of(sysConfigMapper)
                .where(SYS_CONFIG.C_KEY.eq(key))
                .and(SYS_CONFIG.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .one();
        return c == null ? null : c.getCValue();
    }
}
