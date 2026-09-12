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
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 菜单相关对象转换
 *
 * @author codesensi
 * @since 1.0
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
     * <p>
     * 同时作为 toInfoResponse 中 menus 集合的元素级映射被 MapStruct 复用；
     * MenuDTO 无创建时间，路由渲染载荷不携带该字段，显式忽略。
     */
    @Mapping(target = "createTime", ignore = true)
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
     * <p>
     * id 由雪花生成器填充，审计字段由实体监听器填充，builtin 走数据库默认值，均不参与映射。
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysMenu toEntity(MenuInsertDTO menuInsertDTO);

    /**
     * MenuUpdateDTO → SysMenu
     * <p>
     * 审计字段由实体监听器维护；type 属分组级属性，创建后不可修改，均不参与映射。
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysMenu toEntity(MenuUpdateDTO menuUpdateDTO);

}
