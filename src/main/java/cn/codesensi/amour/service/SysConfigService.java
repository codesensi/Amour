package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.entity.SysConfig;

import java.util.List;

/**
 * 运行时配置查询服务。
 * <p>
 * 从 sys_config 表实时读取 {@code app.*} 业务配置，实现配置集中管理与热更新——
 * 修改数据库中的数据后无需重启应用即可生效。
 * <p>
 * 配置值统一以字符串返回（{@code valueType} 仅作类型标注），调用侧可据此自行完成类型转换；
 * 不存在或停用的配置键不会出现在查询结果中。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysConfigService {

    /**
     * 从 config 缓存读取指定配置键当前启用（status=启用）的配置；未命中时回源查库并回填缓存。
     *
     * @param key 配置键
     * @return 启用中的配置实体；不存在或停用返回 {@code null}
     */
    SysConfig oneByKey(String key);

    /**
     * 按配置键集合批量查询配置。
     * <p>
     * {@code keys} 为空（{@code null} 或不含元素）时返回全部启用的配置；
     * 否则逐个按键查询，仅返回存在且启用的配置。
     *
     * @param keys 配置键集合（app 之下的点分路径，如 {@code captcha.enabled}、{@code captcha.image-expire}）；
     *             为空时查询全部
     * @return 配置 DTO 列表；无命中时返回空列表
     */
    List<ConfigDTO> listByKeys(List<String> keys);

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
