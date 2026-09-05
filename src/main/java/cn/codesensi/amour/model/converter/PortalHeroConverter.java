package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.PortalHeroResultDTO;
import cn.codesensi.amour.model.dto.PortalHeroUserDTO;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.response.PortalHeroResponse;
import cn.codesensi.amour.model.response.PortalHeroUserResponse;
import org.mapstruct.Mapper;

/**
 * 门户主角相关对象转换
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface PortalHeroConverter {

    /**
     * SysUser → HeroUserDTO（入参为 null 时返回 null）
     */
    PortalHeroUserDTO toUserDTO(SysUser sysUser);

    /**
     * HeroResultDTO → HeroResponse（male/female 嵌套复用 HeroUserDTO → HeroUserResponse）
     */
    PortalHeroResponse toResponse(PortalHeroResultDTO portalHeroResultDTO);

    /**
     * HeroUserDTO → HeroUserResponse
     */
    PortalHeroUserResponse toUserResponse(PortalHeroUserDTO portalHeroUserDTO);

}
