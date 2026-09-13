package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件分页查询请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FilePageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 原始文件名(模糊匹配)
     */
    @Size(max = 256, message = "文件名长度不能超过256")
    private String originalName;

    /**
     * 业务类型编码: avatar-用户头像, photo-相册照片, markdown-点滴配图
     */
    private String bizType;

    /**
     * 存储类型: local-本地, oss-对象存储
     */
    private String storageType;

    /**
     * 上传人用户名(模糊匹配)
     */
    @Size(max = 128, message = "上传人长度不能超过128")
    private String creatorName;

    /**
     * 上传时间范围-起(yyyy-MM-dd,含当日)
     */
    private String beginTime;

    /**
     * 上传时间范围-止(yyyy-MM-dd,含当日)
     */
    private String endTime;

    /**
     * 删除标识: 0-未删除(缺省), 1-已删除(回收站)
     */
    private Integer delFlag;

}
