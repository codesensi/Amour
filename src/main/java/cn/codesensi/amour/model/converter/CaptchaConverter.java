package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.CaptchaResultDTO;
import cn.codesensi.amour.model.response.CaptchaResponse;
import org.mapstruct.Mapper;

/**
 * 验证码相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface CaptchaConverter {

    /**
     * CaptchaResultDTO → CaptchaResponse
     */
    CaptchaResponse toResponse(CaptchaResultDTO captchaResultDTO);

}
