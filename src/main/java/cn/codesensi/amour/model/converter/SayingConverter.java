package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.SayingResultDTO;
import cn.codesensi.amour.model.response.SayingResponse;
import org.mapstruct.Mapper;

/**
 * 一言相关对象转换
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface SayingConverter {

    /**
     * SayingResultDTO → SayingResponse
     */
    SayingResponse toResponse(SayingResultDTO sayingResultDTO);

}
