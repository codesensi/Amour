package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.CaptchaConverter;
import cn.codesensi.amour.model.dto.CaptchaResultDTO;
import cn.codesensi.amour.model.response.CaptchaResponse;
import cn.codesensi.amour.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码相关接口 前端控制器
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping()
public class CaptchaController {

    private final CaptchaService captchaService;
    private final CaptchaConverter captchaConverter;

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public CaptchaResponse captcha() {
        CaptchaResultDTO captchaResultDTO = captchaService.captcha();
        return captchaConverter.toResponse(captchaResultDTO);
    }
}
