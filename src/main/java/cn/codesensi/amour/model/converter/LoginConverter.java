package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.LoginDTO;
import cn.codesensi.amour.model.dto.LoginResultDTO;
import cn.codesensi.amour.model.request.LoginRequest;
import cn.codesensi.amour.model.response.LoginResponse;
import org.mapstruct.Mapper;

/**
 * 登录相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface LoginConverter {

    /**
     * LoginAccountRequest → LoginAccountDTO
     */
    LoginDTO toDTO(LoginRequest request);

    /**
     * LoginResultDTO → LoginResponse
     */
    LoginResponse toResponse(LoginResultDTO loginResultDTO);

}
