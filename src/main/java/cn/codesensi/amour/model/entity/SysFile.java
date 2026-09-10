package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件记录实体。
 * <p>
 * 对应 {@code sys_file} 表，登记上传文件的存储路径、指纹与业务归属；
 * path 为相对存储 key（本地相对 base-path，OSS 为 object key），完整访问 URL 由运行期构造。
 *
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_file")
public class SysFile extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（同时用作物理文件名与访问 URL 中的文件ID）
     */
    @Id
    private Long id;

    /**
     * 原始文件名（清洗后的纯净文件名，下载时还原）
     */
    private String originalName;

    /**
     * 相对存储 key：本地为 base-path 下相对路径，OSS 为 object key，形如 avatar/202609/{id}.png
     */
    private String path;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件MD5值
     */
    private String md5;

    /**
     * 存储类型: local-本地, oss-对象存储
     */
    private String storageType;

    /**
     * 文件扩展名（全小写）
     */
    private String extension;

    /**
     * 文件类型（Content-Type）
     */
    private String contentType;

    /**
     * 业务来源: avatar-用户头像, markdown-点滴配图, photo-相册照片
     */
    private String bizType;

    /**
     * 业务关联ID（文件被业务采纳时回填，如头像对应用户ID）
     */
    private Long bizId;

}
