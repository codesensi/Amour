package cn.codesensi.amour.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传响应。
 *
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class FileUploadResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 文件访问地址（形如 /file/view/{id}）
     */
    private String url;

    /**
     * 原始文件名
     */
    private String originalName;

}
