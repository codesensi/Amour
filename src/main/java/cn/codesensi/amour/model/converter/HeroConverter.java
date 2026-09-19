package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.HeroResultDTO;
import cn.codesensi.amour.model.dto.HeroUserDTO;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.model.response.HeroResponse;
import cn.codesensi.amour.model.response.HeroUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 门户主角相关对象转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HeroConverter {

    /**
     * SysUser → HeroUserDTO（入参为 null 时返回 null）
     */
    HeroUserDTO toUserDTO(SysUser sysUser);

    /**
     * HeroResultDTO → HeroResponse（male/female 嵌套复用 HeroUserDTO → HeroUserResponse）
     */
    HeroResponse toResponse(HeroResultDTO heroResultDTO);

    /**
     * HeroUserDTO → HeroUserResponse
     */
    HeroUserResponse toUserResponse(HeroUserDTO heroUserDTO);

}
