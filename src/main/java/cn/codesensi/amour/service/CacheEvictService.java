package cn.codesensi.amour.service;

import cn.codesensi.amour.common.util.CacheUtil;

import java.util.List;

/**
 * 缓存失效服务 —— 集中承载项目内各业务缓存的写后显式失效入口。
 * <p>
 * 各业务缓存（user/role/perm/menu/dict/config）均采用「读时回源 + 写侧显式失效」策略，
 * 失效逻辑原先分散在各业务 Service 中，现统一收拢至本接口，一处定义各缓存的失效入口。
 * <p>
 * 本接口方法仅为纯粹的缓存操作，事务提交时序由调用方经
 * {@link CacheUtil#evictAfterCommit(Runnable)} 注册控制（无活跃事务时立即执行）；
 * 缓存未注册/未就绪时静默返回，与读取侧的降级策略对齐。
 *
 * @author codesensi
 * @since 1.0
 */
public interface CacheEvictService {

    /**
     * 失效指定用户的用户信息缓存（user 缓存，Key 为用户ID）。
     *
     * @param userIds 用户ID列表
     */
    void evictUserCache(List<Long> userIds);

    /**
     * 失效指定用户的角色编码缓存（role 缓存，Key 为用户ID）。
     *
     * @param userIds 用户ID列表
     */
    void evictRoleCache(List<Long> userIds);

    /**
     * 失效指定用户的权限编码缓存（perm 缓存，Key 为用户ID）。
     *
     * @param userIds 用户ID列表
     */
    void evictPermCache(List<Long> userIds);

    /**
     * 失效指定用户的路由菜单缓存（menu 缓存，Key 为用户ID）。
     *
     * @param userIds 用户ID列表
     */
    void evictMenuCache(List<Long> userIds);

    /**
     * 失效指定编码的字典缓存（dict 缓存，Key 为字典编码）。
     *
     * @param codes 字典编码列表
     */
    void evictDictCache(List<String> codes);

    /**
     * 失效指定配置键的配置缓存（config 缓存，Key 为配置键）。
     *
     * @param keys 待失效的配置键集合（app 之下的点分路径）
     */
    void evictConfigCache(List<String> keys);

    /**
     * 清空指定缓存的全部条目。
     * <p>
     * 适用于缓存内容被全体调用方共享、无法按键精准失效的场景
     * （如菜单变更后的 perm/menu 缓存）。
     *
     * @param cacheName 基础缓存名（对应 CacheConst 中的常量，经 withAppEnv 拼接前缀）
     */
    void clearCache(String cacheName);

}
