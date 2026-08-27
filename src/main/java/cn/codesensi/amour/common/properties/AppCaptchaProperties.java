package cn.codesensi.amour.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.captcha")
public class AppCaptchaProperties {

    /**
     * 验证码开关
     */
    private boolean enabled = true;

    /**
     * 验证码类型
     */
    private Type type;

    /**
     * 图形验证码类型，默认 SPEC（png 静态图）
     */
    private ImageType imageType = ImageType.SPEC;

    /**
     * 图形验证码过期时间，单位秒。5分钟
     */
    private Long imageExpire = 5L * 60;

    /**
     * 短信验证码过期时间，单位秒。15分钟
     */
    private Long smsExpire = 15L * 60;

    /**
     * 验证码长度
     */
    private Integer smsLength = 6;

    /**
     * 验证码类型
     */
    public enum Type {
        /**
         * 短信验证码
         */
        SMS,
        /**
         * 图形验证码
         */
        IMAGE;


        Type() {
        }
    }

    /**
     * 图形验证码类型
     */
    public enum ImageType {
        /**
         * png
         */
        SPEC,
        /**
         * gif
         */
        GIF,
        /**
         * 中文
         */
        CHINESE,
        /**
         * 中文gif
         */
        CHINESE_GIF,
        /**
         * 算术
         */
        ARITHMETIC;

        ImageType() {
        }
    }
}
