package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.CacheNameEnum;
import cn.codesensi.amour.common.enums.DataModuleEnum;
import cn.codesensi.amour.common.enums.DataScopeEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysRoleDataScopeMapper;
import cn.codesensi.amour.model.entity.SysRoleDataScope;
import cn.codesensi.amour.service.DataScopeService;
import cn.codesensi.amour.service.SysUserRoleService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysRoleDataScopeTableDef.SYS_ROLE_DATA_SCOPE;

/**
 * 数据范围策略服务实现 —— 行级数据权限的唯一判定入口。
 * <p>
 * 判定顺序：超级管理员硬编码短路 → data-scope 缓存（Key 为角色ID）→ 策略表 → 兜底 SELF；
 * 多角色按模块并集取宽（any all → all）。工程红线与判定语义详见
 * {@link DataScopeService} 接口说明。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DataScopeServiceImpl implements DataScopeService {

    private final SysRoleDataScopeMapper sysRoleDataScopeMapper;
    private final SysUserRoleService sysUserRoleService;
    private final CacheManager cacheManager;

    /**
     * 解析当前登录人对指定模块的可见范围。
     *
     * @param module 业务模块
     * @return 可见范围档位（超管恒为 ALL）
     */
    @Override
    public DataScopeEnum resolveVisible(DataModuleEnum module) {
        return resolve(module).visibleScope();
    }

    /**
     * 解析当前登录人对指定模块的可改范围。
     *
     * @param module 业务模块
     * @return 可改范围档位（超管恒为 ALL）
     */
    @Override
    public DataScopeEnum resolveEditable(DataModuleEnum module) {
        return resolve(module).editableScope();
    }

    /**
     * 解析当前登录人在指定模块的行过滤条件值。
     * <p>
     * 可见范围为 SELF 时返回当前登录用户ID，ALL 时返回 null；
     * 配合 {@code QueryColumn.eq(value, condition)} 的条件缺省忽略语义接入查询装配。
     *
     * @param module 业务模块
     * @return 需限定的归属人ID；不过滤行时为 null
     */
    @Override
    public Long visibleOwnerFilter(DataModuleEnum module) {
        return resolveVisible(module) == DataScopeEnum.ALL ? null : StpUtil.getLoginIdAsLong();
    }

    /**
     * 判定当前登录人是否可见指定归属的行。
     * <p>
     * 可见范围为 ALL 直接可见；SELF 时要求行归属人为当前登录人
     * （归属为 null 的种子数据一律视为非本人，安全兜底）。
     *
     * @param module   业务模块
     * @param rowOwner 行归属人ID（历史数据可能为 null）
     * @return true 可见；false 不可见
     */
    @Override
    public boolean canSee(DataModuleEnum module, Long rowOwner) {
        if (resolveVisible(module) == DataScopeEnum.ALL) {
            return true;
        }
        return ObjUtil.equal(StpUtil.getLoginIdAsLong(), rowOwner);
    }

    /**
     * 判定当前登录人是否可修改指定归属的行。
     * <p>
     * 超管直接放行；可改范围为 ALL 放行；SELF 时要求行归属人为当前登录人
     * （归属为 null 的种子数据一律视为非本人，安全兜底）。
     *
     * @param module   业务模块
     * @param rowOwner 行归属人ID（历史数据可能为 null）
     * @return true 可修改；false 不可修改
     */
    @Override
    public boolean canEdit(DataModuleEnum module, Long rowOwner) {
        if (resolveEditable(module) == DataScopeEnum.ALL) {
            return true;
        }
        return ObjUtil.equal(StpUtil.getLoginIdAsLong(), rowOwner);
    }

    /**
     * 写保护断言：当前登录人不可修改时抛出业务异常。
     *
     * @param module   业务模块
     * @param rowOwner 行归属人ID（历史数据可能为 null）
     */
    @Override
    public void assertEditable(DataModuleEnum module, Long rowOwner) {
        if (!canEdit(module, rowOwner)) {
            throw new BusinessException("无权操作该数据");
        }
    }

    /**
     * 解析当前登录人在指定模块的范围档位对。
     * <p>
     * 超管角色硬编码短路（不查表、不进缓存）；普通角色逐角色解析（按角色ID经
     * data-scope 缓存加速）后并集取宽。
     *
     * @param module 业务模块
     * @return 范围档位对
     */
    private ScopePair resolve(DataModuleEnum module) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<String> roleCodes = sysUserRoleService.listRoleCodeByUserId(userId);
        // 超管硬编码短路：不依赖策略表与缓存状态
        if (roleCodes.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return new ScopePair(DataScopeEnum.ALL, DataScopeEnum.ALL);
        }
        // 普通角色：逐角色解析后并集取宽（any all → all）
        DataScopeEnum visible = DataScopeEnum.SELF;
        DataScopeEnum editable = DataScopeEnum.SELF;
        for (Long roleId : sysUserRoleService.listRoleIdsByUserId(userId)) {
            ScopePair pair = loadByRole(roleId).getOrDefault(module, new ScopePair(DataScopeEnum.SELF, DataScopeEnum.SELF));
            visible = widen(visible, pair.visibleScope());
            editable = widen(editable, pair.editableScope());
        }
        return new ScopePair(visible, editable);
    }

    /**
     * 加载单角色全部模块的范围档位映射（经 data-scope 缓存加速，Key 为角色ID）。
     *
     * @param roleId 角色ID
     * @return 模块 → 范围档位对映射（无配置行的模块由调用方兜底）
     */
    private Map<DataModuleEnum, ScopePair> loadByRole(Long roleId) {
        return CacheUtil.load(cacheManager, CacheNameEnum.DATA_SCOPE.getCode(), roleId, this::loadByRoleFromDb);
    }

    /**
     * 从库中加载单角色全部模块的范围档位映射。
     * <p>
     * 防御性过滤模块键与档位取值非法的行（保存侧已校验，此处兜底脏数据不致 NPE）。
     *
     * @param roleId 角色ID
     * @return 模块 → 范围档位对映射
     */
    private Map<DataModuleEnum, ScopePair> loadByRoleFromDb(Long roleId) {
        List<SysRoleDataScope> scopes = QueryChain.of(sysRoleDataScopeMapper)
                .where(SYS_ROLE_DATA_SCOPE.ROLE_ID.eq(roleId))
                .list();
        return scopes.stream()
                .map(scope -> {
                    DataModuleEnum module = BaseEnum.fromCode(DataModuleEnum.class, scope.getModule());
                    DataScopeEnum visible = BaseEnum.fromCode(DataScopeEnum.class, scope.getVisibleScope());
                    DataScopeEnum editable = BaseEnum.fromCode(DataScopeEnum.class, scope.getEditableScope());
                    return ObjUtil.isNull(module) || ObjUtil.isNull(visible) || ObjUtil.isNull(editable)
                            ? null
                            : Map.entry(module, new ScopePair(visible, editable));
                })
                .filter(ObjUtil::isNotNull)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 档位取宽：任一为 ALL 即 ALL（并集语义）。
     *
     * @param a 档位一
     * @param b 档位二
     * @return 较宽的档位
     */
    private DataScopeEnum widen(DataScopeEnum a, DataScopeEnum b) {
        return a == DataScopeEnum.ALL || b == DataScopeEnum.ALL ? DataScopeEnum.ALL : DataScopeEnum.SELF;
    }
}
