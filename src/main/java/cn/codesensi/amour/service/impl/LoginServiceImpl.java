package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.ValidationException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.model.dto.LoginDTO;
import cn.codesensi.amour.model.dto.LoginResultDTO;
import cn.codesensi.amour.model.entity.SysUser;
import cn.codesensi.amour.service.LoginService;
import cn.codesensi.amour.service.SysConfigService;
import cn.codesensi.amour.service.SysUserService;
import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.SysUserTableDef.SYS_USER;

/**
 * 登录接口实现。
 * <p>
 * 账号（用户名/手机号/邮箱）+ 密码登录：按需校验图形验证码 → 校验账号密码 →
 * 校验账号封禁状态 → 执行登录并返回令牌信息。
 * 验证码开关实时读取 sys_config 的 {@code captcha.enabled} 配置，支持热更新。
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final SysUserService sysUserService;
    private final SysConfigService sysConfigService;
    private final CacheManager cacheManager;

    /**
     * 登录。
     *
     * @param loginDTO 登录用户信息
     * @return 登录成功后信息
     */
    @Override
    public LoginResultDTO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 校验必填项
        if (StrUtil.isBlank(username)) {
            throw new ValidationException("账号不能为空");
        }
        if (StrUtil.isBlank(password)) {
            throw new ValidationException("密码不能为空");
        }

        // 校验验证码（开关缺失/停用时视为关闭）
        boolean captchaEnabled = sysConfigService.listByKeys(List.of(ConfigKeyEnum.CAPTCHA_ENABLED.getCode())).stream()
                .anyMatch(config -> Boolean.parseBoolean(config.getConfigValue()));
        if (captchaEnabled) {
            checkCaptcha(loginDTO);
        }

        // 校验用户及密码（用户名/手机号/邮箱任一匹配即可登录）
        SysUser sysUser = sysUserService.queryChain()
                .select(SYS_USER.ID, SYS_USER.USERNAME, SYS_USER.PHONE, SYS_USER.EMAIL, SYS_USER.NICKNAME, SYS_USER.PASSWORD)
                .where(SYS_USER.USERNAME.eq(username))
                .or(SYS_USER.PHONE.eq(username))
                .or(SYS_USER.EMAIL.eq(username))
                .one();
        if (ObjUtil.isNull(sysUser) || StrUtil.isBlank(sysUser.getPassword())
                || !BCrypt.checkpw(password, sysUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        Long userId = sysUser.getId();
        // 校验账户是否封禁
        StpUtil.checkDisable(userId);
        // 登录
        StpUtil.login(userId);

        // 构建登录响应
        LoginResultDTO loginResultDTO = new LoginResultDTO();
        loginResultDTO.setAccessToken(StpUtil.getTokenValue());
        long accessTokenTimeout = StpUtil.getTokenTimeout();
        if (accessTokenTimeout == -1) {
            loginResultDTO.setExpires(-1L); // 永不过期
        } else {
            loginResultDTO.setExpires(Instant.now().plusSeconds(accessTokenTimeout).toEpochMilli());
        }
        loginResultDTO.setTokenName(SaManager.getConfig().getTokenName());
        loginResultDTO.setTokenPrefix(SaManager.getConfig().getTokenPrefix());
        return loginResultDTO;
    }

    /**
     * 退出登录。
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 校验图形验证码。
     * <p>
     * 读取 captcha 缓存中 captchaKey 对应的答案并立即失效（保证一次性使用），再与提交内容比对；
     * 缓存未注册属于配置错误，直接抛出业务异常。
     *
     * @param loginDTO 登录用户信息
     */
    private void checkCaptcha(LoginDTO loginDTO) {
        if (StrUtil.isBlank(loginDTO.getCaptchaKey())) {
            throw new ValidationException("验证码唯一标识不能为空");
        }
        if (StrUtil.isBlank(loginDTO.getCaptchaValue())) {
            throw new ValidationException("验证码不能为空");
        }
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.CAPTCHA));
        if (cache == null) {
            throw new BusinessException("验证码缓存未注册，请检查缓存配置");
        }
        // Spring Cache 抽象无 getAndDelete，以 get + evict 组合实现取即删
        Cache.ValueWrapper wrapper = cache.get(loginDTO.getCaptchaKey());
        cache.evict(loginDTO.getCaptchaKey());
        if (wrapper == null) {
            throw new BusinessException("验证码不存在");
        }
        if (!loginDTO.getCaptchaValue().equals(wrapper.get())) {
            throw new BusinessException("验证码错误");
        }
    }
}
