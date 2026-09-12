package cn.codesensi.amour.common.core;

import cn.codesensi.amour.common.util.LoginUserUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
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
 * <p>
 * 实现实体监听器：插入/更新时自动填充 {@code creator}/{@code updater} 为当前登录用户
 * （门户访客、异步线程等未登录场景保持 null，且不覆盖调用方已显式设置的值）。
 *
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable, InsertListener, UpdateListener {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识:0-未删除，1-已删除
     */
    private Integer delFlag;

    /**
     * 插入时填充创建人：取当前登录用户（未登录场景保持 null）。
     *
     * @param entity 待插入的实体
     */
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity base) {
            Long userId = LoginUserUtil.getLoginIdOrNull();
            if (userId != null && base.getCreator() == null) {
                base.setCreator(userId);
            }
        }
    }

    /**
     * 更新时填充更新人：取当前登录用户（未登录场景保持 null）。
     *
     * @param entity 待更新的实体
     */
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity base) {
            Long userId = LoginUserUtil.getLoginIdOrNull();
            if (userId != null && base.getUpdater() == null) {
                base.setUpdater(userId);
            }
        }
    }

}
