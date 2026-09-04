package cn.codesensi.amour.service;


import cn.codesensi.amour.model.dto.CaptchaResultDTO;

/**
 * 验证码接口
 */
public interface CaptchaService {

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    CaptchaResultDTO genCaptcha();

}
