package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.properties.AppProperties;
import cn.codesensi.amour.model.dto.SayingResultDTO;
import cn.codesensi.amour.model.dto.SayingTextDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.service.SayingService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 一言查询服务实现。
 * <p>
 * 通过 Hutool 以服务端身份调用上游接口（不携带浏览器特征请求头，规避上游对浏览器跨域调用的 403 拦截）：
 * 优先调用随机一言接口（{@code app.uapi-saying-random}，sys_config 配置了 {@code uapi-key}
 * 时携带 {@code X-API-KEY} 请求头）；调用失败降级为一言接口（{@code app.uapi-saying}，不携带
 * 请求密钥，响应仅含 text 字段，映射为文案、出处与作者为空）；两级上游均未配置或均失败时返回
 * 空 DTO。地址类配置取自 yml（改动需重启），密钥取自 sys_config（管理端修改热更新）。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SayingServiceImpl implements SayingService {

    private final SysConfigService sysConfigService;

    private final AppProperties appProperties;

    /**
     * {@inheritDoc}
     * <p>
     * 一言产品语义为每次随机，不做缓存，避免同一时间窗口返回相同内容。
     */
    @Override
    public SayingResultDTO getSaying() {
        String sayingUrl = appProperties.getUapiSayingRandom();
        if (StrUtil.isBlank(sayingUrl)) {
            log.debug("uapi-saying-random 未配置，直接降级 uapi-saying");
            return fallbackByUapiSaying();
        }
        String sayingResponse;
        try {
            // 密钥实时读取 sys_config（管理端修改后事务提交即失效缓存，下次请求即生效），空时不携带请求头
            SysConfig apiKeyConfig = sysConfigService.oneByKey(ConfigKeyEnum.UAPI_KEY.getCode());
            String apiKey = apiKeyConfig == null ? null : apiKeyConfig.getConfigValue();
            HttpRequest request = HttpRequest.get(sayingUrl);
            if (StrUtil.isNotBlank(apiKey)) {
                request.header(AppConst.UAPI_KEY_HEADER, apiKey);
            }
            // try-with-resources 及时释放 HttpResponse 底层连接，避免资源泄漏
            try (HttpResponse response = request.timeout(appProperties.getUapiTimeout()).execute()) {
                sayingResponse = response.body();
            }
        } catch (Exception e) {
            // 上游网络异常(超时/连接失败)降级，不阻断门户调用
            log.warn("uapi-saying-random 调用失败，降级 uapi-saying", e);
            return fallbackByUapiSaying();
        }
        log.debug("uapi-saying-random 响应：{}", sayingResponse);
        if (StrUtil.isBlank(sayingResponse)) {
            log.warn("uapi-saying-random 响应为空，降级 uapi-saying");
            return fallbackByUapiSaying();
        }
        SayingResultDTO sayingResultDTO;
        try {
            sayingResultDTO = JSONUtil.toBean(sayingResponse, SayingResultDTO.class);
        } catch (Exception e) {
            log.warn("uapi-saying-random 响应解析失败，降级 uapi-saying：{}", sayingResponse, e);
            return fallbackByUapiSaying();
        }
        if (StrUtil.isBlank(sayingResultDTO.getContent())) {
            log.warn("uapi-saying-random 响应缺少文案字段，降级 uapi-saying：{}", sayingResponse);
            return fallbackByUapiSaying();
        }
        return sayingResultDTO;
    }

    /**
     * 降级获取一言：调用 uapi-saying（不携带请求密钥），响应仅含 text 字段，映射为文案，出处与作者为空。
     *
     * @return 一言数据；uapi-saying 也未配置或失败时为空 DTO（响应数据全为空）
     */
    private SayingResultDTO fallbackByUapiSaying() {
        String sayingUrl = appProperties.getUapiSaying();
        if (StrUtil.isBlank(sayingUrl)) {
            log.warn("uapi-saying 未配置，一言响应为空");
            return new SayingResultDTO();
        }
        String sayingResponse;
        try {
            sayingResponse = HttpUtil.get(sayingUrl, appProperties.getUapiTimeout());
        } catch (Exception e) {
            log.warn("uapi-saying 调用失败，一言响应为空", e);
            return new SayingResultDTO();
        }
        log.debug("uapi-saying 响应：{}", sayingResponse);
        if (StrUtil.isBlank(sayingResponse)) {
            log.warn("uapi-saying 响应为空");
            return new SayingResultDTO();
        }
        try {
            SayingTextDTO sayingTextDTO = JSONUtil.toBean(sayingResponse, SayingTextDTO.class);
            if (StrUtil.isBlank(sayingTextDTO.getText())) {
                log.warn("uapi-saying 响应缺少文案字段：{}", sayingResponse);
                return new SayingResultDTO();
            }
            return new SayingResultDTO().setContent(sayingTextDTO.getText());
        } catch (Exception e) {
            log.warn("uapi-saying 响应解析失败：{}", sayingResponse, e);
            return new SayingResultDTO();
        }
    }

}
