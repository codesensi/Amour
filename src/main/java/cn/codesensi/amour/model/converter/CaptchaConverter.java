package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.CaptchaResultDTO;
import cn.codesensi.amour.model.response.CaptchaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 验证码相关对象转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CaptchaConverter {

    /**
     * CaptchaResultDTO → CaptchaResponse
     */
    CaptchaResponse toResponse(CaptchaResultDTO captchaResultDTO);

}
