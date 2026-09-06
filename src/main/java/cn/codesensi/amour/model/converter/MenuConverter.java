package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.MenuDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.response.MenuResponse;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 菜单相关对象转换
 *
 * @author codesensi
 * @since 2026-09-06
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    /**
     * SysMenu → MenuDTO
     */
    MenuDTO toDTO(SysMenu sysMenu);

    /**
     * List<SysMenu> → List<MenuDTO>
     */
    List<MenuDTO> toListDTO(List<SysMenu> sysMenus);

    /**
     * MenuDTO → MenuResponse
     */
    MenuResponse toResponse(MenuDTO menuDTO);

    /**
     * SysMenu → MenuResponse
     */
    MenuResponse toResponse(SysMenu sysMenu);

    /**
     * List<SysMenu> → List<MenuResponse>
     */
    List<MenuResponse> toListResponse(List<SysMenu> sysMenus);

}
