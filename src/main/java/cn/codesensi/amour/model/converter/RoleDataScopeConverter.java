package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.RoleDataScopeDTO;
import cn.codesensi.amour.model.dto.RoleDataScopeSaveDTO;
import cn.codesensi.amour.model.request.RoleDataScopeAssignRequest;
import cn.codesensi.amour.model.response.RoleDataScopeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 角色数据范围策略相关对象转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleDataScopeConverter {

    /**
     * RoleDataScopeSaveRequest → RoleDataScopeSaveDTO（嵌套 items 同名映射）
     */
    RoleDataScopeSaveDTO toSaveDTO(RoleDataScopeAssignRequest request);

    /**
     * List<RoleDataScopeDTO> → List<RoleDataScopeResponse>
     */
    List<RoleDataScopeResponse> toListResponse(List<RoleDataScopeDTO> list);

}
