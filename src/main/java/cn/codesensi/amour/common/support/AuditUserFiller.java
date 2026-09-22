package cn.codesensi.amour.common.support;

import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.dto.AuditUserAware;
import cn.codesensi.amour.model.entity.SysUser;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 审计用户回填器 —— 创建人/更新人用户名的统一批量回填入口。
 * <p>
 * 收集行数据中的 creator/updater 主键后仅查询一次用户表（避免逐行查询），
 * 再按 {@link AuditUserAware} 契约写回用户名；输入可空安全（未登录来源的
 * 记录无审计人、行集合为空时直接返回）。文件/画册/清单/纪念日/足迹等
 * 管理端分页共用本组件。
 *
 * @author codesensi
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AuditUserFiller {

    private final SysUserMapper sysUserMapper;

    /**
     * 批量回填行数据的创建人/更新人用户名。
     *
     * @param rows 行数据集合（DTO 已实现 {@link AuditUserAware} 契约）
     * @param <T>  行数据类型
     */
    public <T extends AuditUserAware> void fill(List<T> rows) {
        if (CollUtil.isEmpty(rows)) {
            return;
        }

        // 收集本页出现过的创建人/更新人主键（去重、去空）
        Set<Long> userIds = rows.stream()
                .flatMap(row -> Stream.of(row.getCreator(), row.getUpdater()))
                .filter(ObjUtil::isNotNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(userIds)) {
            return;
        }

        // username 理论非空（登录账号），防御 toMap 对 null value 抛 NPE
        List<SysUser> sysUserList = sysUserMapper.selectListByIds(userIds);
        if (CollUtil.isEmpty(sysUserList)) {
            return;
        }
        Map<Long, String> usernameMap = sysUserList.stream()
                .filter(user -> ObjUtil.isNotNull(user.getUsername()))
                .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername, (a, b) -> a));

        rows.forEach(row -> {
            // 主键可能为空（未登录来源记录），且不可变 Map 拒绝 null key 查询，须先行判空
            if (CollUtil.isNotEmpty(usernameMap)) {
                if (ObjUtil.isNotNull(row.getCreator())) {
                    row.setCreatorName(usernameMap.get(row.getCreator()));
                }
                if (ObjUtil.isNotNull(row.getUpdater())) {
                    row.setUpdaterName(usernameMap.get(row.getUpdater()));
                }
            }
        });
    }
}
