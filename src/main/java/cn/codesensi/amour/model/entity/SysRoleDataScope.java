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
 * 角色数据范围策略实体。
 * <p>
 * 对应 {@code sys_role_data_scope} 表，按「角色 × 业务模块」承载数据范围的
 * 可见/可改档位配置，为数据权限隔离（DataScopeService）的策略数据源；
 * 超级管理员角色（code=admin）在判定层硬编码短路为 all/all，不入本表；
 * 无配置行的角色由判定层按最小权限兜底为 self/self。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_role_data_scope")
public class SysRoleDataScope extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 业务模块键（DataModuleEnum）
     */
    private String module;

    /**
     * 可见范围: all-全部数据, self-仅本人
     */
    private String visibleScope;

    /**
     * 可改范围: all-全部数据, self-仅本人（不得宽于可见范围）
     */
    private String editableScope;

}
