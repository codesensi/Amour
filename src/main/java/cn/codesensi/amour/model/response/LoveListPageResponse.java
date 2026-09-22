package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 恋爱清单分页查询行数据响应结果（管理端，完整字段）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class LoveListPageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 清单内容
     */
    private String content;

    /**
     * 完成状态: 0-未完成， 1-已完成
     */
    private Integer done;

    /**
     * 纪念照地址（完成项可选）
     */
    private String photo;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

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
