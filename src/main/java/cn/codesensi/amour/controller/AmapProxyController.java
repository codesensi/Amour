package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.RateLimit;
import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RbacConst;
import cn.codesensi.amour.common.enums.RateLimitKey;
import cn.codesensi.amour.model.dto.AmapProxyResultDTO;
import cn.codesensi.amour.service.AmapProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * 高德 Web 服务代理 前端控制器。
 * <p>
 * JS API 安全密钥的官方推荐保护方式：前端设置
 * {@code window._AMapSecurityConfig = { serviceHost: "/_AMapService" }} 后，
 * JS SDK 的 Web 服务请求自动携带该前缀，由本控制器转发至高德并在服务端附加 jscode。
 * <p>
 * 不标注 {@code @ApiResponseBody}——高德响应必须原样返回，
 * 统一响应包装会破坏 JS SDK 的 JSON 解析；本路径免登录（JS SDK 发起的请求
 * 无法附带 Authorization 头），防滥用依赖 {@code @RateLimit} 按 IP 限流。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(RbacConst.AMAP_PROXY_PREFIX)
public class AmapProxyController {

    /**
     * JSONP 形态（query 携带 callback）的脚本响应类型，浏览器仅执行 JS 类型的脚本响应
     */
    private static final MediaType APPLICATION_JAVASCRIPT = new MediaType("application", "javascript", StandardCharsets.UTF_8);

    private final AmapProxyService amapProxyService;

    /**
     * 转发高德 Web 服务请求（免登录 + IP 限流；JS SDK 服务请求均为 GET）。
     * <p>响应体为高德原始 JSON/JSONP，Content-Type 按请求形态自适应。
     *
     * @param request 当前请求，用于解析代理前缀之后的高德内部路径
     * @return 高德原始响应体
     */
    @RateLimit(key = RateLimitKey.AMAP_PROXY, fallbackLimit = 30, fallbackWindowSeconds = 60)
    @GetMapping("/**")
    public ResponseEntity<String> forward(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String path = uri.substring(request.getContextPath().length() + RbacConst.AMAP_PROXY_PREFIX.length());
        // Referer 须透传给高德：JS API key 的服务请求凭"白名单域名页面的来源"放行
        AmapProxyResultDTO result = amapProxyService.forward(request.getHeader(AppConst.REFERER_HEADER), path, request.getQueryString());
        MediaType contentType = result.isJsonp() ? APPLICATION_JAVASCRIPT : MediaType.APPLICATION_JSON;
        return ResponseEntity.ok().contentType(contentType).body(result.getBody());
    }

}
