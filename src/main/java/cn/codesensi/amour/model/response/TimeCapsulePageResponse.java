package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 时间胶囊行响应结果（管理端分页）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class TimeCapsulePageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 信件内容
     */
    private String content;

    /**
     * 解锁时间（到点后门户可见全文;经全局 Jackson 配置序列化为 yyyy-MM-dd HH:mm:ss）
     */
    private LocalDateTime openTime;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人用户名（服务层批量回填;未登录来源记录为空）
     */
    private String creatorName;

    /**
     * 更新人用户名（服务层批量回填;未发生过更新的记录为空）
     */
    private String updaterName;

}
