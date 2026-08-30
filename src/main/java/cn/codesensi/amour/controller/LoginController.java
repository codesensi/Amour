package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.LoginConverter;
import cn.codesensi.amour.model.dto.LoginDTO;
import cn.codesensi.amour.model.dto.LoginResultDTO;
import cn.codesensi.amour.model.request.LoginRequest;
import cn.codesensi.amour.model.response.LoginResponse;
import cn.codesensi.amour.service.LoginService;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 前端控制器
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@ApiResponseBody
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;
    private final LoginConverter loginConverter;

    /**
     * 登录
     */
    @SaIgnore
    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        LoginDTO loginDTO = loginConverter.toDTO(request);
        LoginResultDTO loginResultDTO = loginService.login(loginDTO);
        return loginConverter.toResponse(loginResultDTO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public void logout() {
        loginService.logout();
    }

}
