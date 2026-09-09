package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.dto.ConfigPageDTO;
import cn.codesensi.amour.model.dto.ConfigUpdateDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import com.mybatisflex.core.paginate.Page;

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
     * {@code keys} 为空（{@code null} 或不含元素）时返回空列表；
     * 否则逐个按键查询，仅返回存在且启用的配置。
     *
     * @param keys 配置键集合（app 之下的点分路径，如 {@code captcha.enabled}、{@code captcha.image-type}）；
     *             为空时返回空列表
     * @return 配置 DTO 列表；无命中时返回空列表
     */
    List<ConfigDTO> listByKeys(List<String> keys);

    /**
     * 分页查询配置（管理端，含禁用条目与完整字段）。
     *
     * @param pageDTO 分页查询参数
     * @return 配置实体分页结果
     */
    Page<SysConfig> page(ConfigPageDTO pageDTO);

    /**
     * 修改配置（管理端）。
     * <p>
     * 仅允许修改配置值、状态与备注；配置键、值类型与分组由代码侧约定，不可变更。
     * 修改成功后失效该配置键的 config 缓存，实现"驻留不过期"策略下的热更新。
     *
     * @param updateDTO 修改参数
     */
    void update(ConfigUpdateDTO updateDTO);

}
