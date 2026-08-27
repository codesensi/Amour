package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.entity.SysConfig;
import cn.codesensi.amour.mapper.SysConfigMapper;
import cn.codesensi.amour.service.ConfigService;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cn.codesensi.amour.entity.table.SysConfigTableDef.SYS_CONFIG;

/**
 * 运行时配置查询服务实现。
 * <p>
 * 运行时实时查库，从 sys_config 表读取以 point 路径（如 {@code name}、{@code captcha.sms-expire}）
 * 作为 {@code config_key} 存储的配置，实现热更新——修改数据库后无需重启即可生效。
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

    private final SysConfigMapper sysConfigMapper;

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
     * 从 sys_config 表查询指定配置键当前启用（status=启用）的配置值。
     *
     * @param key 配置键
     * @return 配置值字符串；不存在或停用返回 {@code null}
     */
    private String raw(String key) {
        SysConfig c = QueryChain.of(sysConfigMapper)
                .where(SYS_CONFIG.CONFIG_KEY.eq(key))
                .and(SYS_CONFIG.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .one();
        return c == null ? null : c.getConfigValue();
    }
}
