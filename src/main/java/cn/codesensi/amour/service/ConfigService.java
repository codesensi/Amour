package cn.codesensi.amour.service;

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
}
