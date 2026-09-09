package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.service.CacheEvictService;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 缓存失效服务实现。
 * <p>
 * 各业务缓存的失效动作统一经 {@link #evictByKeys(String, Collection)} 与
 * {@link #clearCache(String)} 收敛执行，避免各业务 Service 重复编写
 * 「取缓存 → 判空 → 逐键失效」的样板逻辑。
 *
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CacheEvictServiceImpl implements CacheEvictService {

    private final CacheManager cacheManager;

    /**
     * 失效指定用户的用户信息缓存。
     *
     * @param userIds 用户ID列表
     */
    @Override
    public void evictUserCache(List<Long> userIds) {
        evictByKeys(CacheConst.USER, userIds);
    }

    /**
     * 失效指定用户的角色编码缓存。
     *
     * @param userIds 用户ID列表
     */
    @Override
    public void evictRoleCache(List<Long> userIds) {
        evictByKeys(CacheConst.ROLE, userIds);
    }

    /**
     * 失效指定用户的权限编码缓存。
     *
     * @param userIds 用户ID列表
     */
    @Override
    public void evictPermCache(List<Long> userIds) {
        evictByKeys(CacheConst.PERM, userIds);
    }

    /**
     * 失效指定用户的路由菜单缓存。
     *
     * @param userIds 用户ID列表
     */
    @Override
    public void evictMenuCache(List<Long> userIds) {
        evictByKeys(CacheConst.MENU, userIds);
    }

    /**
     * 失效指定编码的字典缓存。
     *
     * @param codes 字典编码列表
     */
    @Override
    public void evictDictCache(List<String> codes) {
        evictByKeys(CacheConst.DICT, codes);
    }

    /**
     * 失效指定配置键的配置缓存。
     *
     * @param keys 待失效的配置键集合
     */
    @Override
    public void evictConfigCache(List<String> keys) {
        evictByKeys(CacheConst.CONFIG, keys);
    }

    /**
     * 清空指定缓存的全部条目；缓存未注册/未就绪时静默返回。
     *
     * @param cacheName 基础缓存名（经 {@link CacheUtil#withAppEnv(String)} 拼接项目名_运行环境前缀）
     */
    @Override
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(cacheName));
        if (cache != null) {
            cache.clear();
            log.debug("已清空缓存：cache={}", cacheName);
        }
    }

    /**
     * 按缓存名与键集合逐个失效缓存条目。
     * <p>
     * 键集合为空或缓存未注册/未就绪时静默返回（与读取侧降级策略对齐）；
     * 集合中的 {@code null} 元素会被跳过，避免缓存层对空键抛出异常。
     *
     * @param cacheName 基础缓存名（经 {@link CacheUtil#withAppEnv(String)} 拼接项目名_运行环境前缀）
     * @param keys      待失效的键集合
     */
    private void evictByKeys(String cacheName, Collection<?> keys) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(cacheName));
        if (cache == null) {
            return;
        }
        for (Object key : keys) {
            if (key != null) {
                cache.evict(key);
            }
        }
        log.debug("已失效缓存条目：cache={}，keys={}", cacheName, keys);
    }

}
