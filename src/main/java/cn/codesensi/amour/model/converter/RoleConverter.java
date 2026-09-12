package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysRole;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.RolePageResponse;
import cn.codesensi.amour.model.response.RoleResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 角色相关对象转换
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * RoleInsertRequest → RoleInsertDTO
     */
    RoleInsertDTO toInsertDTO(RoleInsertRequest request);

    /**
     * RoleUpdateRequest → RoleUpdateDTO
     */
    RoleUpdateDTO toUpdateDTO(RoleUpdateRequest request);

    /**
     * RoleChangeStatusRequest → RoleChangeStatusDTO
     */
    RoleChangeStatusDTO toChangeStatusDTO(RoleChangeStatusRequest request);

    /**
     * AssignMenusRequest → AssignMenusDTO
     */
    AssignMenusDTO toAssignMenusDTO(AssignMenusRequest request);

    /**
     * RoleInsertDTO → SysRole
     * <p>
     * id 由雪花生成器填充，审计字段由实体监听器填充，status/builtin 走数据库默认值，均不参与映射。
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysRole toEntity(RoleInsertDTO roleInsertDTO);

    /**
     * RoleUpdateDTO → SysRole
     * <p>
     * 审计字段由实体监听器维护；code 创建后不可修改，status 由独立的状态接口维护，均不参与映射。
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    SysRole toEntity(RoleUpdateDTO roleUpdateDTO);

    /**
     * RolePageRequest → RolePageDTO
     */
    RolePageDTO toPageDTO(RolePageRequest request);

    /**
     * Page<SysRole> → Page<RolePageResponse>
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<RolePageResponse> toPageResponse(Page<SysRole> page);

    /**
     * List<SysRole> → List<RoleResponse>
     */
    List<RoleResponse> toListResponse(List<SysRole> list);

}
