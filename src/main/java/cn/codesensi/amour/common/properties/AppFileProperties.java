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
 * 本类的 storage 仅作为配置缺失时的兜底默认值；OSS
 *
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "app.file")
public class AppFileProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储方式: local-本地, oss-对象存储（运行时以 sys_config 的 file.storage 为准，此处为兜底值）
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
