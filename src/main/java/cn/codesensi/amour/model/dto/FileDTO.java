package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件分页查询行数据 DTO（服务层承载，控制层经转换器映射为 FilePageResponse）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件扩展名（全小写）
     */
    private String extension;

    /**
     * 文件类型（Content-Type）
     */
    private String contentType;

    /**
     * 存储类型: local-本地， oss-对象存储
     */
    private String storageType;

    /**
     * 存储路径（相对 key）
     */
    private String path;

    /**
     * 业务来源: infra-基础设施， avatar-用户头像， photo-相册照片， markdown-点滴配图
     */
    private String bizType;

    /**
     * 业务关联ID（文件被业务采纳时回填）
     */
    private Long bizId;

    /**
     * 上传人ID
     */
    private Long creator;

    /**
     * 上传人用户名（服务层按本页 creator 批量回填）
     */
    private String creatorName;

    /**
     * 上传时间
     */
    private LocalDateTime createTime;

}
