package cn.codesensi.amour.service;

/**
 * 运行时配置查询服务：从 sys_config 表实时读取 app.* 业务配置（热更新）。
 */
public interface ConfigService {

    String getString(String key);

    boolean getBool(String key);

    int getInt(String key);
}
