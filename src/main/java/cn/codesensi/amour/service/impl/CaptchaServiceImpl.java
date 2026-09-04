package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.enums.ImageType;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.model.dto.CaptchaResultDTO;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.service.CaptchaService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.util.IdUtil;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 验证码接口实现。
 * <p>
 * 依据 sys_config 表中 {@code captcha.image-type} 配置生成对应类型的图形验证码，
 * 验证码答案以 captchaKey 为键存入 captcha 缓存（过期时间由缓存定义决定，见
 * {@link CacheConst#CAPTCHA}），接口仅返回 captchaKey 与验证码图片的 Base64 编码。
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CaptchaServiceImpl implements CaptchaService {

    /**
     * 图形验证码类型 → 验证码实例工厂的映射表。
     * <p>
     * {@link EnumMap} 保证每个 {@link ImageType} 都有对应条目，
     * 新增类型时编译期即可发现缺失。
     * </p>
     */
    private static final Map<ImageType, Supplier<Captcha>> CAPTCHA_FACTORY = new EnumMap<>(ImageType.class);

    static {
        CAPTCHA_FACTORY.put(ImageType.SPEC, SpecCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.GIF, GifCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.CHINESE, ChineseCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.CHINESE_GIF, ChineseGifCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.ARITHMETIC, ArithmeticCaptcha::new);
    }

    private final SysConfigService sysConfigService;
    private final CacheManager cacheManager;

    /**
     * 生成图形验证码。
     * <p>
     * 处理流程：读取 {@code captcha.image-type} 配置创建验证码实例 → 生成验证码内容与图片 →
     * 答案存入 captcha 缓存 → 返回 captchaKey 与图片 Base64。
     *
     * @return 验证码唯一标识与验证码图片 Base64
     * @throws SystemException 图形验证码类型配置错误或缓存未注册时抛出
     */
    @Override
    public CaptchaResultDTO genCaptcha() {
        // 读取图形验证码类型配置（实时读取支持热更新；未配置时默认算术验证码）
        String imageTypeCode = sysConfigService.listByKeys(List.of(ConfigKeyEnum.CAPTCHA_IMAGE_TYPE.getCode()))
                .stream().findFirst()
                .map(ConfigDTO::getConfigValue)
                .orElse(ImageType.ARITHMETIC.getCode());
        ImageType imageType = BaseEnum.fromCode(ImageType.class, imageTypeCode);
        if (imageType == null) {
            throw new SystemException("不支持的图形验证码类型：captcha.image-type=" + imageTypeCode);
        }

        // 编译期确定的工厂创建，无需反射
        Captcha captcha = CAPTCHA_FACTORY.get(imageType).get();

        // 算术验证码额外记录运算公式便于调试
        if (captcha instanceof ArithmeticCaptcha arithmeticCaptcha) {
            log.debug("算术验证码生成：formula={}", arithmeticCaptcha.getArithmeticString());
        }

        String keyUuid = IdUtil.fastSimpleUUID();
        String text = captcha.text();
        log.debug("图形验证码生成：key={}，code={}", keyUuid, text);

        // 放入缓存：验证码答案以 captchaKey 为键存入，过期时间由 captcha 缓存定义决定
        Cache cache = captchaCache();
        cache.put(keyUuid, text);

        // 返回结果
        CaptchaResultDTO captchaResultDTO = new CaptchaResultDTO();
        captchaResultDTO.setCaptchaKey(keyUuid);
        captchaResultDTO.setCaptchaValue(captcha.toBase64());
        return captchaResultDTO;
    }

    /**
     * 获取 captcha 缓存实例。
     * <p>
     * 验证码答案必须落缓存才能在后续校验时比对，缓存未注册属于配置错误，直接抛出系统异常。
     *
     * @return captcha 缓存
     */
    private Cache captchaCache() {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.CAPTCHA));
        if (cache == null) {
            throw new BusinessException("验证码缓存未注册，请检查缓存配置");
        }
        return cache;
    }
}
