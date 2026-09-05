package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.enums.EnableEnum;
import cn.codesensi.amour.common.enums.GenderEnum;
import cn.codesensi.amour.mapper.SysRoleMapper;
import cn.codesensi.amour.mapper.SysUserMapper;
import cn.codesensi.amour.mapper.SysUserRoleMapper;
import cn.codesensi.amour.model.converter.PortalHeroConverter;
import cn.codesensi.amour.model.dto.PortalHeroResultDTO;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.service.PortalHeroService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;

/**
 * 门户主角服务实现。
 * <p>
 * 男女主不落地独立配置：以 sys_role 中 code=hero 的内置角色为标识，
 * 绑定该角色的启用用户即候选主角，同性别按创建时间倒序取最晚注册的一个。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class PortalHeroServiceImpl implements PortalHeroService {

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysUserMapper sysUserMapper;

    private final PortalHeroConverter portalHeroConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public PortalHeroResultDTO getPortalHero() {
        // 1. 启用中的主角角色
        SysRole heroRole = QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ID)
                .where(SYS_ROLE.CODE.eq(RbacConst.ROLE_HERO_CODE))
                .and(SYS_ROLE.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .one();
        if (ObjUtil.isNull(heroRole)) {
            return new PortalHeroResultDTO();
        }

        // 2. 角色绑定的候选用户ID
        List<Long> userIds = QueryChain.of(sysUserRoleMapper)
                .select(SYS_USER_ROLE.USER_ID)
                .where(SYS_USER_ROLE.ROLE_ID.eq(heroRole.getId()))
                .listAs(Long.class);
        if (CollUtil.isEmpty(userIds)) {
            return new PortalHeroResultDTO();
        }

        // 3. 启用中的候选用户按ID倒序：男女主各取最晚注册的一个
        List<SysUser> users = QueryChain.of(sysUserMapper)
                .where(SYS_USER.ID.in(userIds))
                .and(SYS_USER.STATUS.eq(EnableEnum.ENABLE.getCode()))
                .orderBy(SYS_USER.ID.desc())
                .list();
        SysUser male = users.stream()
                .filter(user -> GenderEnum.MALE.getCode().equals(user.getGender()))
                .findFirst()
                .orElse(null);
        SysUser female = users.stream()
                .filter(user -> GenderEnum.FEMALE.getCode().equals(user.getGender()))
                .findFirst()
                .orElse(null);
        return new PortalHeroResultDTO()
                .setMale(portalHeroConverter.toUserDTO(male))
                .setFemale(portalHeroConverter.toUserDTO(female));
    }

}
