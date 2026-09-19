package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传结果 DTO（服务层承载，控制层经转换器映射为 FileUploadResponse）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class FileUploadResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
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
