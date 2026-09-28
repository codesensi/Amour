package cn.codesensi.amour.service;

import cn.codesensi.amour.common.enums.DataModuleEnum;
import cn.codesensi.amour.common.enums.DataScopeEnum;

/**
 * 数据范围策略服务 —— 全系统行级数据权限的唯一判定入口（PDP）。
 * <p>
 * 按「角色 × 业务模块」解析数据的可见/可改范围，判定顺序：
 * <ol>
 *     <li>超级管理员角色（{@code RbacConst#ROLE_ADMIN_CODE}）硬编码短路为 all/all，
 *     不查策略表、不进缓存；</li>
 *     <li>普通角色按策略表配置解析（经 data-scope 缓存加速，Key 为角色ID）；</li>
 *     <li>无配置行的模块兜底为最小权限 self；多角色并集取宽（any all → all）。</li>
 * </ol>
 * 工程红线：任何业务代码禁止自行比较 creator 做行归属判定，必须复用本服务，
 * 避免归属判定散落各处形成权限漏洞。
 *
 * @author codesensi
 * @since 1.0
 */
public interface DataScopeService {

    /**
     * 解析当前登录人对指定模块的可见范围。
     *
     * @param module 业务模块
     * @return 可见范围档位（超管恒为 ALL）
     */
    DataScopeEnum resolveVisible(DataModuleEnum module);

    /**
     * 解析当前登录人对指定模块的可改范围。
     *
     * @param module 业务模块
     * @return 可改范围档位（超管恒为 ALL）
     */
    DataScopeEnum resolveEditable(DataModuleEnum module);

    /**
     * 解析当前登录人在指定模块的行过滤条件值。
     * <p>
     * 供管理端分页查询按既有条件装配风格接入：
     * 可见范围为 SELF 时返回当前登录用户ID（作为行归属等值过滤值），
     * ALL 时返回 null（配合 {@code QueryColumn.eq(value, condition)} 的条件缺省忽略语义，
     * 不过滤行）。归属列由调用方按模块语义选择（审计 creator 或业务归属列，如日记/点滴的
     * user_id）。示例：
     * <pre>{@code
     * .and(PORTAL_XXX.CREATOR.eq(dataScopeService.visibleOwnerFilter(DataModuleEnum.XXX), ObjUtil::isNotNull))
     * }</pre>
     *
     * @param module 业务模块
     * @return 需限定的归属人ID；不过滤行时为 null
     */
    Long visibleOwnerFilter(DataModuleEnum module);

    /**
     * 判定当前登录人是否可见指定归属的行。
     * <p>
     * 可见范围为 ALL 或行归属人为当前登录人时可见；供胶囊封存遮罩等
     * 「行可见但字段遮蔽」的特例在行级细判时调用。
     *
     * @param module   业务模块
     * @param rowOwner 行归属人ID（历史数据可能为 null）
     * @return true 可见；false 不可见
     */
    boolean canSee(DataModuleEnum module, Long rowOwner);

    /**
     * 判定当前登录人是否可修改指定归属的行。
     * <p>
     * 可改范围为 SELF 且行归属人非当前登录人（含归属为 null 的种子数据）时拒绝；
     * 供 update/delete/changeHidden 等写操作在存在性校验后调用。
     *
     * @param module   业务模块
     * @param rowOwner 行归属人ID（历史数据可能为 null）
     * @return true 可修改；false 不可修改
     */
    boolean canEdit(DataModuleEnum module, Long rowOwner);

    /**
     * 写保护断言：当前登录人不可修改时直接抛出业务异常。
     *
     * @param module   业务模块
     * @param rowOwner 行归属人ID（历史数据可能为 null）
     */
    void assertEditable(DataModuleEnum module, Long rowOwner);

    /**
     * 单角色在某模块的范围档位对（缓存载体的最小单元）。
     *
     * @param visibleScope  可见范围
     * @param editableScope 可改范围
     */
    record ScopePair(DataScopeEnum visibleScope, DataScopeEnum editableScope) {
    }
}
