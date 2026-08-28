package cn.codesensi.amour.service;

import java.util.List;

/**
 * 运行时配置查询服务。
 * <p>
 * 从 sys_config 表实时读取 {@code app.*} 业务配置，实现配置集中管理与热更新——
 * 修改数据库中的数据后无需重启应用即可生效。
 * <p>
 * 各方法均以配置键（{@code app} 之下的点分路径，如 {@code name}、{@code captcha.sms-expire}）为入参。
 * 当配置在库中不存在或处于停用状态时，返回对应类型的默认值（{@code null}/{@code false}/{@code 0}），
 * 调用侧只需自行决定是否回退到其他默认策略。
 */
public interface ConfigService {

    /**
     * 读取字符串配置。
     *
     * @param key 配置键（app 之下的点分路径）
     * @return 配置值字符串；不存在或停用返回 {@code null}
     */
    String getString(String key);

    /**
     * 读取布尔配置。
     *
     * @param key 配置键（app 之下的点分路径）
     * @return 布尔配置值；不存在或停用返回 {@code false}
     */
    boolean getBool(String key);

    /**
     * 读取整数配置。
     *
     * @param key 配置键（app 之下的点分路径）
     * @return 整数配置值；不存在或停用返回 {@code 0}
     */
    int getInt(String key);

    /**
     * 读取长整数配置。
     *
     * @param key 配置键（app 之下的点分路径）
     * @return 长整数配置值；不存在或停用返回 {@code 0L}
     */
    long getLong(String key);

    /**
     * 失效配置缓存。
     * <p>
     * 供写库侧（如管理端新增/修改/停用 sys_config）在数据变更后调用，实现"驻留不过期"缓存策略下的热更新：
     * <ul>
     *   <li>{@code keys} 为空（{@code null} 或不含元素）时清空整个 config 缓存，适用于批量变更（如初始化、导入）后的全量失效；</li>
     *   <li>{@code keys} 非空时逐个失效对应配置键的缓存。</li>
     * </ul>
     *
     * @param keys 待失效的配置键集合（app 之下的点分路径）；为空时清除全部
     */
    void evictCache(List<String> keys);
}
