package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.properties.AppProperties;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.model.dto.QqInfoResultDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.service.QqInfoService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * QQ 信息查询服务实现。
 * <p>
 * 通过 Hutool 以服务端身份调用上游接口（不携带浏览器特征请求头，规避上游对浏览器跨域调用的 403 拦截）：
 * 优先调用 qq-api（{@code app.uapi-qq}，sys_config 配置了 {@code uapi-key} 时携带
 * {@code X-API-KEY} 请求头）解析真实头像与昵称；任一环节失败（未配置/网络异常/响应空/解析失败/缺头像字段）
 * 降级为 qq-avatar（{@code app.qq-avatar}，QQ 官方头像）按 QQ 号拼接；qq-avatar 也未配置时返回
 * 空 DTO，由前端本地兜底图兜底。地址类配置取自 yml（改动需重启），密钥取自 sys_config（管理端修改热更新）。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QqInfoServiceImpl implements QqInfoService {

    private final SysConfigService sysConfigService;

    private final CacheManager cacheManager;

    private final AppProperties appProperties;

    /**
     * {@inheritDoc}
     * <p>
     * 查询结果写入 qq-info 缓存（写后 15 分钟过期，见 application-dev.yml），
     * 同一 QQ 号在过期前直接复用缓存，避免失焦/翻页的高频调用穿透上游。
     * 降级结果（avatarUrl 非空）同样写入缓存：同一 QQ 号在过期前头像恒定，
     * 密钥修复/上游恢复后最迟一个缓存周期自动切换回真实信息。
     */
    @Override
    public QqInfoResultDTO getQqInfo(String qq) {
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheConst.QQ));
        if (cache == null) {
            throw new SystemException("QQ信息缓存未注册，请检查缓存配置");
        }
        QqInfoResultDTO cached = cache.get(qq, QqInfoResultDTO.class);
        if (cached != null) {
            return cached;
        }
        QqInfoResultDTO result = loadFromQqApi(qq);
        // 降级尽头仍无头像的空结果不写缓存:避免配置补齐后 15 分钟内一直返回空信息
        if (StrUtil.isNotBlank(result.getAvatarUrl())) {
            cache.put(qq, result);
        }
        return result;
    }

    /**
     * 回源查询 QQ 信息：qq-api 优先，任一失败环节降级 qq-avatar 拼接。
     *
     * @param qq QQ 号（6~12 位数字，由控制器完成格式校验）
     * @return 头像地址与昵称；qq-avatar 降级时昵称为空，qq-avatar 未配置时为空 DTO
     */
    private QqInfoResultDTO loadFromQqApi(String qq) {
        String qqApiUrl = appProperties.getUapiQq();
        if (StrUtil.isBlank(qqApiUrl)) {
            log.debug("qq-api 未配置，降级 qq-avatar：qq={}", qq);
            return fallbackByQqAvatar(qq);
        }
        String qqApiResponse;
        try {
            // 密钥实时读取 sys_config（管理端修改后事务提交即失效缓存，下次请求即生效），空时不携带请求头
            SysConfig apiKeyConfig = sysConfigService.oneByKey(ConfigKeyEnum.UAPI_KEY.getCode());
            String apiKey = apiKeyConfig == null ? null : apiKeyConfig.getConfigValue();
            HttpRequest request = HttpRequest.get(
                    String.format(qqApiUrl, URLEncoder.encode(qq, StandardCharsets.UTF_8)));
            if (StrUtil.isNotBlank(apiKey)) {
                request.header(AppConst.UAPI_KEY_HEADER, apiKey);
            }
            // try-with-resources 及时释放 HttpResponse 底层连接,避免资源泄漏
            try (HttpResponse response = request.timeout(appProperties.getUapiTimeout()).execute()) {
                qqApiResponse = response.body();
            }
        } catch (Exception e) {
            // 上游网络异常(超时/连接失败)降级,不阻断门户调用
            log.warn("qq-api 调用失败，降级 qq-avatar：qq={}", qq, e);
            return fallbackByQqAvatar(qq);
        }
        log.debug("qq-api 响应：qq={}，body={}", qq, qqApiResponse);
        if (StrUtil.isBlank(qqApiResponse)) {
            log.warn("qq-api 响应为空，降级 qq-avatar：qq={}", qq);
            return fallbackByQqAvatar(qq);
        }
        QqInfoResultDTO qqInfoResultDTO;
        try {
            qqInfoResultDTO = JSONUtil.toBean(qqApiResponse, QqInfoResultDTO.class);
        } catch (Exception e) {
            log.warn("qq-api 响应解析失败，降级 qq-avatar：{}", qqApiResponse, e);
            return fallbackByQqAvatar(qq);
        }
        // 上游异常载荷(限流/配额耗尽/无效 QQ 等)可能缺少头像字段,判空兜底避免 NPE
        if (StrUtil.isBlank(qqInfoResultDTO.getAvatarUrl())) {
            log.warn("qq-api 响应缺少头像地址，降级 qq-avatar：{}", qqApiResponse);
            return fallbackByQqAvatar(qq);
        }
        if (qqInfoResultDTO.getAvatarUrl().startsWith("http://")) {
            qqInfoResultDTO.setAvatarUrl(qqInfoResultDTO.getAvatarUrl().replace("http://", "https://"));
        }
        return qqInfoResultDTO;
    }

    /**
     * 降级拼接头像地址：按 qq-avatar 模板以 QQ 号拼接（QQ 官方头像，同号恒定），昵称为空。
     *
     * @param qq QQ 号
     * @return 降级结果；qq-avatar 未配置时为空 DTO（响应数据全为空，由前端本地兜底图兜底）
     */
    private QqInfoResultDTO fallbackByQqAvatar(String qq) {
        String qqAvatarUrl = appProperties.getQqAvatar();
        if (StrUtil.isBlank(qqAvatarUrl)) {
            log.warn("qq-avatar 未配置，QQ 信息响应为空：qq={}", qq);
            return new QqInfoResultDTO();
        }
        return new QqInfoResultDTO()
                .setAvatarUrl(String.format(qqAvatarUrl, URLEncoder.encode(qq, StandardCharsets.UTF_8)));
    }

}
