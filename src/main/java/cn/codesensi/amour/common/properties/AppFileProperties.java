package cn.codesensi.amour.common.properties;

import cn.codesensi.amour.common.enums.StorageTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件存储配置 —— 本地目录与对象存储参数。
 * <p>
 * 存储方式运行时以 sys_config 的 {@code file.storage} 为准（支持系统配置页热更新），
 * 本类的 storage 仅作为配置缺失时的兜底默认值；OSS 相关参数为对象存储接入时启用，
 * 接入前仅作预留。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "app.file")
public class AppFileProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储方式: local-本地， oss-对象存储（运行时以 sys_config 的 file.storage 为准，此处为兜底值）
     */
    private String storage = StorageTypeEnum.LOCAL.getCode();

    /**
     * 本地存储配置
     */
    private Local local = new Local();

    /**
     * 对象存储参数（OSS 接入时启用）
     */
    private Oss oss = new Oss();

    /**
     * 预览接口响应的浏览器缓存天数（Cache-Control max-age）。
     * 文件内容与 URL 一一对应不可变，默认缓存一年；已下发到浏览器的缓存无法远程回收，
     * 调整本值仅影响之后的响应。
     */
    private int viewCacheDays = 365;

    /**
     * 本地存储配置。
     */
    @Data
    public static class Local implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 本地存储根目录，实际文件按 {bizType}/{yyyyMM}/{fileId}.{ext} 落盘
         */
        private String basePath = "./data/files";
    }

    /**
     * 对象存储参数。
     */
    @Data
    public static class Oss implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * OSS Endpoint
         */
        private String endpoint;

        /**
         * OSS Bucket
         */
        private String bucket;

        /**
         * Access Key
         */
        private String accessKey;

        /**
         * Secret Key
         */
        private String secretKey;
    }
}
