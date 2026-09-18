package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.AmapProxyResultDTO;

/**
 * 高德 Web 服务代理 —— JS API 安全密钥的官方推荐保护方式。
 * <p>
 * 前端通过 {@code window._AMapSecurityConfig = { serviceHost: "/_AMapService" }} 将
 * JS SDK 的 Web 服务请求指向本服务，由本服务转发至高德 restapi 并在服务端附加
 * 安全密钥（jscode），jscode 全程不下发浏览器。
 *
 * @author codesensi
 * @since 1.0
 */
public interface AmapProxyService {

    /**
     * 转发高德 Web 服务请求。
     *
     * @param referer 浏览器发起请求时的 Referer（JS API key 的服务请求必须携带
     *                来源页面 Referer，高德据此判定调用合法；可空）
     * @param path    高德 restapi 内部路径（如 {@code /v3/geocode/regeo}，不含代理前缀）
     * @param query   原始查询串（可为空）；转发时在服务端附加 jscode 参数
     * @return 转发结果（高德原始响应体 + 是否 JSONP 形态）
     */
    AmapProxyResultDTO forward(String referer, String path, String query);

}
