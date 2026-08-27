package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.entity.SysConfig;
import cn.codesensi.amour.mapper.SysConfigMapper;
import cn.codesensi.amour.service.ConfigService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 运行时配置查询服务实现。
 * <p>
 * 运行时实时查库；未命中或停用时回落到 yml 默认值兜底。
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper sysConfigMapper;

    /**
     * yml 默认值兜底
     */
    private final Map<String, String> ymlDefaults = Map.ofEntries(
            Map.entry("name", "爱慕情侣小站"),
            Map.entry("version", "1.0.0"),
            Map.entry("author", "codesensi"),
            Map.entry("copyright", "2026"),
            Map.entry("avatar", "https://api.dicebear.com/7.x/bottts/svg?seed=%s"),
            Map.entry("demo-mode", "false"),
            Map.entry("captcha.enabled", "false"),
            Map.entry("captcha.type", "image"),
            Map.entry("captcha.image-type", "arithmetic"),
            Map.entry("captcha.image-expire", "300"),
            Map.entry("captcha.sms-expire", "900"),
            Map.entry("captcha.sms-length", "6"));

    @Override
    public String getString(String key) {
        String v = raw(key);
        return v != null ? v : ymlDefaults.get(key);
    }

    @Override
    public boolean getBool(String key) {
        String v = raw(key);
        return Boolean.parseBoolean(v != null ? v : ymlDefaults.getOrDefault(key, "false"));
    }

    @Override
    public int getInt(String key) {
        String v = raw(key);
        return Integer.parseInt(v != null ? v : ymlDefaults.getOrDefault(key, "0"));
    }

    private String raw(String key) {
        SysConfig c = sysConfigMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SysConfig::getConfigKey).eq(key)
                        .and(SysConfig::getIsActive).eq(1));
        return c == null ? null : c.getConfigValue();
    }
}
