package cn.codesensi.amour.common.core;

import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类 —— 业务实体的公共字段基类（审计字段与逻辑删除标识）。
 * <p>
 * 对应数据库各表的 {@code creator}、{@code create_time}、{@code updater}、
 * {@code update_time}、{@code del_flag} 公共列。
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识:0-未删除,1-已删除
     */
    private Integer delFlag;

}
