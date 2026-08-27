package cn.codesensi.amour.context;

import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Data
public class AppEnvContext {

    public static final String SPRING_APPLICATION_NAME = "spring.application.name";
    public static final String DEFAULT_APP_NAME = "app";
    public static final String DEFAULT_ACTIVE_PROFILE = "default";

    private final String appName;
    private final String[] activeProfiles;
    private final String activeProfileStr; // 缓存拼接后的字符串，便于日志打印
    private final String firstActiveProfile;

    public AppEnvContext(Environment environment) {
        // 属性缺失时给出合理默认值
        this.appName = environment.getProperty(SPRING_APPLICATION_NAME, DEFAULT_APP_NAME);
        this.activeProfiles = environment.getActiveProfiles();
        // 如果未激活任何Profile，Spring默认返回 ["default"]
        if (this.activeProfiles.length == 0) {
            this.activeProfileStr = DEFAULT_ACTIVE_PROFILE;
        } else {
            this.activeProfileStr = StringUtils.arrayToCommaDelimitedString(this.activeProfiles);
        }
        this.firstActiveProfile = this.activeProfiles.length > 0 ? this.activeProfiles[0] : DEFAULT_ACTIVE_PROFILE;
    }

}