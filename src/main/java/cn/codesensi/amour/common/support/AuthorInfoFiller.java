package cn.codesensi.amour.common.support;

import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.model.dto.AuthorInfoAware;
import cn.codesensi.amour.model.entity.SysUser;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 作者展示信息回填器 —— 按业务作者 userId 的用户名/昵称/QQ/头像统一批量回填入口。
 * <p>
 * 收集行数据中的 userId 主键后仅查询一次用户表（避免逐行查询），
 * 再按 {@link AuthorInfoAware} 契约写回展示字段；输入可空安全（行集合为空、
 * 无作者主键、用户表无命中时直接返回）。点滴/日记等门户展示分页共用本组件。
 * <p>
 * 与 {@link AuditUserFiller}（审计语义:creator/updater → 用户名）相互独立：
 * 本组件面向门户前台作者展示,业务作者与记录创建人允许不同。
 *
 * @author codesensi
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AuthorInfoFiller {

    private final SysUserMapper sysUserMapper;

    /**
     * 批量回填行数据的作者用户名/昵称/QQ/头像。
     *
     * @param rows 行数据集合（DTO 已实现 {@link AuthorInfoAware} 契约）
     * @param <T>  行数据类型
     */
    public <T extends AuthorInfoAware> void fill(List<T> rows) {
        if (CollUtil.isEmpty(rows)) {
            return;
        }

        // 收集本页出现过的作者主键（去重、去空）
        Set<Long> userIds = rows.stream()
                .map(AuthorInfoAware::getUserId)
                .filter(ObjUtil::isNotNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(userIds)) {
            return;
        }

        List<SysUser> sysUserList = sysUserMapper.selectListByIds(userIds);
        if (CollUtil.isEmpty(sysUserList)) {
            return;
        }
        Map<Long, SysUser> userMap = sysUserList.stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));

        rows.forEach(row -> {
            SysUser user = userMap.get(row.getUserId());
            if (ObjUtil.isNotNull(user)) {
                row.setUsername(user.getUsername());
                row.setNickname(user.getNickname());
                row.setQq(user.getQq());
                row.setAvatar(user.getAvatar());
            }
        });
    }

}
