package cn.codesensi.amour.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件分页查询行数据响应结果
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FilePageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小(字节)
     */
    private Long size;

    /**
     * 文件扩展名(全小写)
     */
    private String extension;

    /**
     * 文件类型(Content-Type)
     */
    private String contentType;

    /**
     * 存储类型: local-本地, oss-对象存储
     */
    private String storageType;

    /**
     * 存储路径(相对 key)
     */
    private String path;

    /**
     * 业务来源: avatar-用户头像, photo-相册照片, markdown-点滴配图
     */
    private String bizType;

    /**
     * 业务关联ID(文件被业务采纳时回填)
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long bizId;

    /**
     * 上传人ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long creator;

    /**
     * 上传人用户名
     */
    private String creatorName;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
