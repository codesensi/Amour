package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.QqInfoResultDTO;
import cn.codesensi.amour.model.response.QqInfoResponse;
import org.mapstruct.Mapper;

/**
 * QQ 信息相关对象转换
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface QqInfoConverter {

    /**
     * QqInfoResultDTO → QqInfoResponse
     */
    QqInfoResponse toResponse(QqInfoResultDTO qqInfoResultDTO);

}
