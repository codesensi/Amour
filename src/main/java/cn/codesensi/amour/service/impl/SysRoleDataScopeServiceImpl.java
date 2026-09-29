package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.DataModuleEnum;
import cn.codesensi.amour.common.enums.DataScopeEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysRoleDataScopeMapper;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.model.dto.RoleDataScopeDTO;
import cn.codesensi.amour.model.dto.RoleDataScopeSaveDTO;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysRoleDataScope;
import cn.codesensi.amour.service.CacheEvictService;
import cn.codesensi.amour.service.SysRoleDataScopeService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysRoleDataScopeTableDef.SYS_ROLE_DATA_SCOPE;
import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;

/**
 * 角色数据范围策略服务实现。
 * <p>
 * 查询按枚举全量补齐（无配置行的模块以最小权限 self 下发，保证配置界面所见即所得）；
 * 保存为整角色覆盖式写法——物理删除旧行后整批插入，规避「唯一索引包含逻辑删除行」
 * 导致的同组重插冲突（对齐 assignMenus 的既有处理）；保存后失效该角色的
 * data-scope 缓存，判定即时生效。超级管理员角色不参与配置（判定层硬编码短路）。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysRoleDataScopeServiceImpl implements SysRoleDataScopeService {

    private final SysRoleDataScopeMapper sysRoleDataScopeMapper;
    private final SysRoleMapper sysRoleMapper;
    private final CacheEvictService cacheEvictService;

    /**
     * 查询角色的全模块数据范围配置。
     *
     * @param roleId 角色ID
     * @return 全模块的范围档位配置列表
     */
    @Override
    public List<RoleDataScopeDTO> listByRoleId(Long roleId) {
        SysRole sysRole = requireRole(roleId);
        boolean superAdmin = RbacConst.ROLE_ADMIN_CODE.equals(sysRole.getCode());

        // 超管角色：硬编码 all/all 的展示口径，配置不落库
        if (superAdmin) {
            return buildItems(module -> new ScopeValues(DataScopeEnum.ALL, DataScopeEnum.ALL), true);
        }

        // 普通角色：库中配置 + 未配置行按最小权限 self 补齐
        Map<String, SysRoleDataScope> scopeMap = QueryChain.of(sysRoleDataScopeMapper)
                .where(SYS_ROLE_DATA_SCOPE.ROLE_ID.eq(roleId))
                .list().stream()
                .collect(Collectors.toMap(SysRoleDataScope::getModule, Function.identity(), (a, b) -> a));
        return buildItems(module -> {
            SysRoleDataScope scope = scopeMap.get(module.getCode());
            return scope == null
                    ? new ScopeValues(DataScopeEnum.SELF, DataScopeEnum.SELF)
                    : new ScopeValues(
                    BaseEnum.fromCode(DataScopeEnum.class, scope.getVisibleScope()),
                    BaseEnum.fromCode(DataScopeEnum.class, scope.getEditableScope()));
        }, false);
    }

    /**
     * 保存角色的全模块数据范围配置（整角色覆盖式保存）。
     * <p>
     * 校验链：角色存在 → 非超级管理员 → 模块键合法 → 可改范围不宽于可见范围；
     * 物理删除旧行与插入新行处于同一事务（唯一索引包含逻辑删除行，须绕过逻辑删除删旧）；
     * 缓存失效注册到事务提交后执行，避免提交前其他请求回源查库把中间状态重新写入缓存。
     *
     * @param saveDTO 保存参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignDataScope(RoleDataScopeSaveDTO saveDTO) {
        Long roleId = saveDTO.getRoleId();
        SysRole sysRole = requireRole(roleId);
        if (RbacConst.ROLE_ADMIN_CODE.equals(sysRole.getCode())) {
            throw new BusinessException("超级管理员持有全部数据权限，无需配置");
        }

        List<RoleDataScopeSaveDTO.RoleDataScopeItemDTO> items = ObjUtil.defaultIfNull(saveDTO.getItems(), List.of());
        List<SysRoleDataScope> entities = items.stream()
                .map(item -> {
                    DataModuleEnum module = BaseEnum.fromCode(DataModuleEnum.class, item.getModule());
                    if (ObjUtil.isNull(module)) {
                        throw new BusinessException("不支持的数据权限模块：" + item.getModule());
                    }
                    DataScopeEnum visible = BaseEnum.fromCode(DataScopeEnum.class, item.getVisibleScope());
                    DataScopeEnum editable = BaseEnum.fromCode(DataScopeEnum.class, item.getEditableScope());
                    if (ObjUtil.isNull(visible) || ObjUtil.isNull(editable)) {
                        throw new BusinessException("不支持的数据范围档位：" + item.getVisibleScope() + "/" + item.getEditableScope());
                    }
                    // 一致性不变式：可改范围不得宽于可见范围（self 可见时仅可 self 可改）
                    if (visible == DataScopeEnum.SELF && editable == DataScopeEnum.ALL) {
                        throw new BusinessException("模块「" + module.getDesc() + "」的可改范围不得宽于可见范围");
                    }
                    SysRoleDataScope entity = new SysRoleDataScope();
                    entity.setRoleId(roleId);
                    entity.setModule(module.getCode());
                    entity.setVisibleScope(visible.getCode());
                    entity.setEditableScope(editable.getCode());
                    return entity;
                })
                .toList();

        // 物理删除旧行（本表唯一索引包含逻辑删除行，逻辑删除的旧行会占用索引导致重插冲突）
        LogicDeleteManager.execWithoutLogicDelete(
                () -> sysRoleDataScopeMapper.deleteByQuery(QueryWrapper.create().where(SYS_ROLE_DATA_SCOPE.ROLE_ID.eq(roleId))));
        if (CollUtil.isNotEmpty(entities)) {
            entities.forEach(sysRoleDataScopeMapper::insert);
        }

        // 失效该角色的数据范围缓存（提交后执行，对齐既有写侧失效惯例）
        CacheUtil.evictAfterCommit(() -> {
            log.debug("角色数据范围保存完成：roleId={}，模块数={}，失效缓存", roleId, entities.size());
            cacheEvictService.evictDataScopeCache(List.of(roleId));
        });
    }

    /**
     * 校验角色存在并返回。
     *
     * @param roleId 角色ID
     * @return 角色实体
     */
    private SysRole requireRole(Long roleId) {
        SysRole sysRole = QueryChain.of(sysRoleMapper)
                .where(SYS_ROLE.ID.eq(roleId))
                .one();
        if (ObjUtil.isNull(sysRole)) {
            throw new BusinessException("角色不存在");
        }
        return sysRole;
    }

    /**
     * 按枚举顺序组装全模块响应（含模块说明）。
     *
     * @param valuesResolver 模块 → 档位值解析器
     * @param superAdmin     是否超级管理员角色
     * @return 全模块配置列表
     */
    private List<RoleDataScopeDTO> buildItems(Function<DataModuleEnum, ScopeValues> valuesResolver, boolean superAdmin) {
        List<RoleDataScopeDTO> responses = new ArrayList<>();
        for (DataModuleEnum module : DataModuleEnum.values()) {
            ScopeValues values = valuesResolver.apply(module);
            RoleDataScopeDTO response = new RoleDataScopeDTO();
            response.setModule(module.getCode());
            response.setModuleDesc(module.getDesc());
            response.setVisibleScope(values.visible().getCode());
            response.setEditableScope(values.editable().getCode());
            response.setSuperAdmin(superAdmin);
            responses.add(response);
        }
        return responses;
    }

    /**
     * 档位值载体（模块解析器内部用）。
     *
     * @param visible  可见范围
     * @param editable 可改范围
     */
    private record ScopeValues(DataScopeEnum visible, DataScopeEnum editable) {
    }
}
