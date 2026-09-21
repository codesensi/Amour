package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.VisitTotalDTO;
import cn.codesensi.amour.model.response.VisitTotalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 门户访问统计转换器（MapStruct 编译期生成实现类）。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitConverter {

    /**
     * 累计访问统计 DTO → 门户响应（字段同名自动映射）。
     *
     * @param dto 累计访问统计 DTO
     * @return 门户累计访问统计响应对象
     */
    VisitTotalResponse toResponse(VisitTotalDTO dto);

}
