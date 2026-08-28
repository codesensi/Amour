package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.model.response.CacheEntryResponse;
import cn.codesensi.amour.model.response.CacheResponse;
import cn.codesensi.amour.service.CacheService;
import com.github.benmanes.caffeine.cache.Policy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存查询服务实现。
 * <p>
 * 遍历 {@link CacheManager} 中注册的全部缓存，经 {@link CaffeineCache#getNativeCache()}
 * 获取原生 Caffeine 缓存后，通过 {@code asMap()} 视图读取全部条目，通过 {@code policy()}
 * 读取过期策略与每条目的剩余过期时间，用于运行期查看缓存内容。
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;

    /**
     * 查询全部 Caffeine 缓存内容。
     * <p>
     * 处理流程：遍历缓存管理器中注册的全部缓存 → 读取过期策略 → 读取各缓存条目及剩余过期时间 →
     * 空值哨兵还原为 {@code null} → 按缓存名排序返回。
     *
     * @return 各缓存的名称、过期策略与条目列表；无缓存时返回空列表
     */
    @Override
    public List<CacheResponse> listAll() {
        List<CacheResponse> result = new ArrayList<>();
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (!(cache instanceof CaffeineCache caffeineCache)) {
                // 非 Caffeine 实现的缓存无法直接读取条目，跳过
                continue;
            }

            CacheResponse response = new CacheResponse();
            response.setCacheName(cacheName);
            fillPolicy(response, caffeineCache);
            response.setEntries(listEntries(caffeineCache));
            result.add(response);
        }
        // 按缓存名排序，保证输出顺序稳定
        result.sort(Comparator.comparing(CacheResponse::getCacheName));
        return result;
    }

    /**
     * 读取缓存实际生效的过期策略并填充到响应对象。
     * <p>
     * 从原生 Caffeine 缓存的 {@code policy()} 读取（而非回读 yml 配置），保证与运行期
     * 实际生效的策略一致；未配置的维度不赋值，呈现在响应中为 {@code null}（表示不限制）。
     *
     * @param response      响应对象
     * @param caffeineCache Spring 缓存的 Caffeine 实现
     */
    private void fillPolicy(CacheResponse response, CaffeineCache caffeineCache) {
        Policy<Object, Object> policy = caffeineCache.getNativeCache().policy();
        policy.expireAfterWrite().ifPresent(fixed -> response.setExpireAfterWrite(fixed.getExpiresAfter(TimeUnit.SECONDS)));
        policy.expireAfterAccess().ifPresent(fixed -> response.setExpireAfterAccess(fixed.getExpiresAfter(TimeUnit.SECONDS)));
        policy.eviction().ifPresent(eviction -> response.setMaximumSize(eviction.getMaximum()));
    }

    /**
     * 读取缓存全部条目，并附每条目的剩余过期时间。
     * <p>
     * 剩余过期时间经 {@link Policy#getEntryIfPresentQuietly(Object)} 获取条目元数据后计算，
     * 该方法为只读查询，不会刷新条目的访问时间；缓存配置了任一过期维度时其值为正数秒，
     * 驻留不过期的缓存呈现在响应中为 {@code null}。
     *
     * @param caffeineCache Spring 缓存的 Caffeine 实现
     * @return 缓存条目列表
     */
    private List<CacheEntryResponse> listEntries(CaffeineCache caffeineCache) {
        Policy<Object, Object> policy = caffeineCache.getNativeCache().policy();
        // 缓存是否配置了过期维度：均未配置时条目永不过期，无需查询剩余时间
        boolean expirable = policy.expireAfterWrite().isPresent() || policy.expireAfterAccess().isPresent();

        List<CacheEntryResponse> entries = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : caffeineCache.getNativeCache().asMap().entrySet()) {
            CacheEntryResponse entryResponse = new CacheEntryResponse();
            entryResponse.setKey(String.valueOf(entry.getKey()));
            entryResponse.setValue(renderValue(entry.getValue()));
            entryResponse.setRemainExpire(remainExpire(policy, entry.getKey(), expirable));
            entries.add(entryResponse);
        }
        return entries;
    }

    /**
     * 计算指定条目的剩余过期时间（秒）。
     *
     * @param policy    原生缓存的策略视图
     * @param key       缓存键
     * @param expirable 缓存是否配置了过期维度
     * @return 剩余过期秒数；缓存不过期或条目已被移除时返回 {@code null}
     */
    private Long remainExpire(Policy<Object, Object> policy, Object key, boolean expirable) {
        if (!expirable) {
            return null;
        }
        Policy.CacheEntry<Object, Object> entry = policy.getEntryIfPresentQuietly(key);
        // 条目在遍历间隙被淘汰/移除时视为已无剩余时间
        return entry == null ? null : entry.expiresAfter().toSeconds();
    }

    /**
     * 渲染缓存条目值：空值哨兵还原为 {@code null}，其余原样返回。
     *
     * @param value 缓存条目原始值
     * @return 呈现给调用侧的值
     */
    private Object renderValue(Object value) {
        return value == CacheConst.NULL_MARKER ? null : value;
    }
}
