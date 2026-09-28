package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色数据范围策略响应。
 * <p>
 * 配置抽屉的单行数据：模块与该模块的可见/可改档位；无配置行的模块由服务端
 * 按最小权限兜底为 self 下发；超级管理员角色整单 superAdmin=true（前端置灰展示）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class RoleDataScopeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务模块键（DataModuleEnum）
     */
    private String module;

    /**
     * 模块说明
     */
    private String moduleDesc;

    /**
     * 可见范围: all/self
     */
    private String visibleScope;

    /**
     * 可改范围: all/self
     */
    private String editableScope;

    /**
     * 是否超级管理员角色（超管数据范围硬编码为 all/all，不可配置）
     */
    private Boolean superAdmin;
}
