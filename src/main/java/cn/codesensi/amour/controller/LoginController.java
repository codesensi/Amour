package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.LoginConverter;
import cn.codesensi.amour.model.dto.LoginDTO;
import cn.codesensi.amour.model.dto.LoginResultDTO;
import cn.codesensi.amour.model.request.LoginRequest;
import cn.codesensi.amour.model.response.LoginResponse;
import cn.codesensi.amour.service.LoginService;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 前端控制器
 *
 * @author codesensi
 * @since 1.0
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
    @Log(module = "系统管理", operation = "登录", type = LogTypeEnum.LOGIN)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        LoginDTO loginDTO = loginConverter.toDTO(request);
        LoginResultDTO loginResultDTO = loginService.login(loginDTO);
        return loginConverter.toResponse(loginResultDTO);
    }

    /**
     * 退出登录
     */
    @Log(module = "系统管理", operation = "退出登录", type = LogTypeEnum.LOGOUT)
    @PostMapping("/logout")
    public void logout() {
        loginService.logout();
    }

}
