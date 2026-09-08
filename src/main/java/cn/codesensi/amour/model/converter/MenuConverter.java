package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.MenuChangeStatusDTO;
import cn.codesensi.amour.model.dto.MenuDTO;
import cn.codesensi.amour.model.dto.MenuInsertDTO;
import cn.codesensi.amour.model.dto.MenuUpdateDTO;
import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.model.request.MenuChangeStatusRequest;
import cn.codesensi.amour.model.request.MenuInsertRequest;
import cn.codesensi.amour.model.request.MenuUpdateRequest;
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

    /**
     * MenuInsertRequest → MenuInsertDTO
     */
    MenuInsertDTO toInsertDTO(MenuInsertRequest request);

    /**
     * MenuUpdateRequest → MenuUpdateDTO
     */
    MenuUpdateDTO toUpdateDTO(MenuUpdateRequest request);

    /**
     * MenuChangeStatusRequest → MenuChangeStatusDTO
     */
    MenuChangeStatusDTO toChangeStatusDTO(MenuChangeStatusRequest request);

    /**
     * MenuInsertDTO → SysMenu
     */
    SysMenu toEntity(MenuInsertDTO menuInsertDTO);

    /**
     * MenuUpdateDTO → SysMenu
     */
    SysMenu toEntity(MenuUpdateDTO menuUpdateDTO);

}
