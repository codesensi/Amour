package cn.codesensi.amour.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色数据范围策略保存请求参数。
 * <p>
 * 整角色批量保存：items 覆盖该角色全部纳入数据隔离的模块（未提交的模块按未配置处理，
 * 判定层兜底最小权限 self）；超级管理员角色不允许保存（判定层硬编码 all/all）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class RoleDataScopeAssignRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 各模块的范围档位配置
     */
    @Valid
    private List<RoleDataScopeRequest> items;

    /**
     * 单模块的范围档位配置项。
     *
     * @author codesensi
     * @since 1.0
     */
    @Data
    public static class RoleDataScopeRequest implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 业务模块键（DataModuleEnum）
         */
        @NotBlank(message = "业务模块不能为空")
        private String module;

        /**
         * 可见范围: all/self
         */
        @NotBlank(message = "可见范围不能为空")
        private String visibleScope;

        /**
         * 可改范围: all/self（不得宽于可见范围）
         */
        @NotBlank(message = "可改范围不能为空")
        private String editableScope;
    }
}
