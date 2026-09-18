package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 高德服务代理转发结果 DTO —— 响应体与响应形态。
 * <p>
 * body 为高德原始响应文本；jsonp 标识请求是否为 JSONP 形态
 * （query 携带 callback），由响应方据此选择 Content-Type。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class AmapProxyResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 高德原始响应体
     */
    private String body;

    /**
     * 是否 JSONP 形态（query 携带 callback 参数）
     */
    private boolean jsonp;

}
