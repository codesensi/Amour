package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色数据范围策略保存参数。
 * <p>
 * 整角色批量保存：items 覆盖该角色全部纳入数据隔离的模块（未提交的模块按未配置处理，
 * 判定层兜底最小权限 self）；超级管理员角色不允许保存（判定层硬编码 all/all）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class RoleDataScopeSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 各模块的范围档位配置
     */
    private List<RoleDataScopeItemDTO> items;

    /**
     * 单模块的范围档位配置项。
     *
     * @author codesensi
     * @since 1.0
     */
    @Data
    public static class RoleDataScopeItemDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 业务模块键（DataModuleEnum）
         */
        private String module;

        /**
         * 可见范围: all/self
         */
        private String visibleScope;

        /**
         * 可改范围: all/self（不得宽于可见范围）
         */
        private String editableScope;
    }
}
