package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.properties.AppProperties;
import cn.codesensi.amour.model.dto.AmapProxyResultDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.service.AmapProxyService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 高德 Web 服务代理实现。
 * <p>
 * 安全密钥（jscode）实时读取 sys_config 的 {@code security.amap-code}（敏感配置，
 * 不经免登录接口下发），仅在服务端转发时附加；转发目标固定为 restapi.amap.com，
 * 防止路径拼接导致的 SSRF。JS SDK 的服务请求均为 GET，未使用自定义地图服务，
 * 无需转发 webapi.amap.com（官方文档中的可选代理段）。
 * <p>
 * 可预期失败（jscode 未配置、上游网络异常）不抛异常，而是按高德错误语义返回
 * {@code status=0} 的响应体——JS SDK 的回调能立即收到失败状态，前端可即时降级。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AmapProxyServiceImpl implements AmapProxyService {

    /** 高德 JSONP 形态的回调参数标记（query 携带时响应为 JS 脚本） */
    private static final String CALLBACK_PARAM = "callback=";

    private final SysConfigService sysConfigService;

    private final AppProperties appProperties;

    @Override
    public AmapProxyResultDTO forward(String referer, String path, String query) {
        // 路径硬校验：拒绝目录穿越，转发目标只允许 restapi 下的固定前缀
        if (StrUtil.isBlank(path) || path.contains("..")) {
            throw new SystemException("高德服务代理路径非法");
        }
        SysConfig codeConfig = sysConfigService.oneByKey(ConfigKeyEnum.SECURITY_AMAP_CODE.getCode());
        String jscode = ObjUtil.isNull(codeConfig) ? null : codeConfig.getConfigValue();
        if (StrUtil.isBlank(jscode)) {
            return proxyError(query, "高德安全密钥未配置，请在系统配置-安全配置中填写 security.amap-code");
        }
        String url = appProperties.getAmapRestapiUrl() + path
                + (StrUtil.isBlank(query) ? "" : "?" + query)
                + (StrUtil.isBlank(query) ? "?" : "&") + "jscode="
                + URLEncoder.encode(jscode, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.get(url).timeout(appProperties.getAmapTimeout());
        // 透传浏览器 Referer：JS API key 的服务请求须携带来源页面域名，
        // 高德据此识别"JS API 场景的服务调用"，缺失时返回 10009 平台不符
        if (StrUtil.isNotBlank(referer)) {
            request.header(AppConst.REFERER_HEADER, referer);
        }
        try (HttpResponse response = request.execute()) {
            String body = response.body();
            log.debug("高德服务代理转发：path={}，status={}，body={}", path, response.getStatus(), body);
            return buildResult(body, query);
        } catch (Exception e) {
            log.warn("高德服务代理转发失败：path={}", path, e);
            return proxyError(query, "高德服务代理转发失败，请稍后重试");
        }
    }

    /**
     * 构造高德错误语义的响应体（status=0），保持 JSON/JSONP 形态自适应。
     * <p>
     * JSONP 请求的错误响应须包装为 callback(...) 并以 JS 类型返回——
     * 正常响应由高德完成包装，代理自造的错误体必须自行包装，否则回调无法触发。
     *
     * @param query 原始查询串（解析 JSONP 回调名）
     * @param info  错误描述
     * @return 转发结果
     */
    private AmapProxyResultDTO proxyError(String query, String info) {
        ProxyErrorBody body = new ProxyErrorBody();
        body.setInfo(info);
        String json = JSONUtil.toJsonStr(body);
        String callback = extractCallback(query);
        return buildResult(callback != null ? callback + "(" + json + ")" : json, query);
    }

    /**
     * 从 query 中解析 JSONP 回调名。
     * <p>
     * 仅放行合法 JS 标识符字符——回调名会被拼进脚本执行，须拦截注入。
     *
     * @param query 原始查询串（可空）
     * @return 回调名；非 JSONP 请求返回 {@code null}
     */
    private String extractCallback(String query) {
        if (StrUtil.isBlank(query)) {
            return null;
        }
        for (String pair : query.split("&")) {
            if (pair.startsWith(CALLBACK_PARAM)) {
                String callback = pair.substring(CALLBACK_PARAM.length());
                return callback.matches("[A-Za-z0-9_.]+") ? callback : null;
            }
        }
        return null;
    }

    /**
     * 组装转发结果（JSONP 形态判定：query 携带 callback 时响应为 JS 脚本）。
     *
     * @param body  高德原始响应体
     * @param query 原始查询串
     * @return 转发结果
     */
    private AmapProxyResultDTO buildResult(String body, String query) {
        AmapProxyResultDTO result = new AmapProxyResultDTO();
        result.setBody(body);
        result.setJsonp(query != null && query.contains(CALLBACK_PARAM));
        return result;
    }

    /**
     * 高德错误语义的响应体契约 —— 序列化后字段名与高德 restapi 失败响应一致。
     * <p>
     * {@code status=0} 由 JS SDK 判定为检索失败；{@code infocode=PROXY_ERROR} 为代理
     * 自定义码（非高德官方码），便于区分失败来自代理自身还是高德上游。
     */
    @Data
    private static class ProxyErrorBody {

        /** 高德失败状态码 */
        private String status = "0";

        /** 错误描述 */
        private String info;

        /** 代理自定义失败码 */
        private String infocode = "PROXY_ERROR";

    }

}
