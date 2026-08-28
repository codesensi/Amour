package cn.codesensi.amour.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * IP 地址工具类 —— 从请求中解析真实客户端 IP，以及获取本机内网 IPv4 地址。
 * <p>
 * 解析时按优先级依次读取常见代理转发头（X-Forwarded-For 等），
 * 均未命中时回退到 {@code RemoteAddr}。
 */
@Slf4j
public class IpUtil {

    /**
     * 占位值：代理头中无法获取到有效 IP 时的常见占位字符串（不区分大小写比较）。
     */
    private static final String UNKNOWN = "unknown";

    /**
     * IPv4 本地回环地址。
     */
    private static final String LOCALHOST_IP = "127.0.0.1";

    /**
     * IPv6 本地回环地址。
     */
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 多级代理下 X-Forwarded-For 头中多个 IP 之间的分隔符。
     */
    private static final String SEPARATOR = ",";

    /**
     * 内网 IP 前缀集合（RFC1918 私有地址段，用于识别代理服务器）
     */
    private static final Set<String> INTERNAL_IP_SEGMENTS = new HashSet<>(Arrays.asList(
            "10.", "192.168.", "172.16.", "172.17.", "172.18.", "172.19.",
            "172.20.", "172.21.", "172.22.", "172.23.", "172.24.", "172.25.",
            "172.26.", "172.27.", "172.28.", "172.29.", "172.30.", "172.31."
    ));

    /**
     * 获取客户端IP（从当前线程绑定的请求中解析）。
     *
     * @return IP地址
     */
    public static String getIpAddr() {
        return getIpAddr(ServletUtil.getRequest());
    }

    /**
     * 获取真实客户端IP（推荐使用）。
     * <p>
     * 解析优先级：
     * <ol>
     *   <li>{@code X-Forwarded-For}：多级代理下取第一个非内网的 IP，全为内网时取第一个；</li>
     *   <li>其他常见代理头：{@code X-Real-IP}、{@code Proxy-Client-IP} 等；</li>
     *   <li>{@code request.getRemoteAddr()} 兜底，IPv6 回环地址归一化为 {@code 127.0.0.1}。</li>
     * </ol>
     *
     * @param request HTTP 请求，为 {@code null} 时返回 {@code unknown}
     * @return 真实客户端 IP；无法解析时返回 {@code unknown}
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        // 1. 优先检查 X-Forwarded-For（处理多级代理）
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // X-Forwarded-For 可能包含多个IP，格式为: 客户端IP, 代理1IP, 代理2IP...
            // 取第一个非内网的IP作为真实客户端IP
            String[] ips = ip.split(SEPARATOR);
            for (String s : ips) {
                String trimIp = s.trim();
                if (isValidIp(trimIp) && !isInternalIp(trimIp)) {
                    return trimIp;
                }
            }
            // 如果全是内网IP，返回第一个
            return ips[0].trim();
        }

        // 2. 检查其他常见的代理头
        String[] headers = {"X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        for (String header : headers) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return ip;
            }
        }

        // 3. 最后使用 RemoteAddr 兜底
        ip = request.getRemoteAddr();
        // 处理 IPv6 本地回环地址
        if (LOCALHOST_IPV6.equals(ip)) {
            return LOCALHOST_IP;
        }
        return ip;
    }


    /**
     * 获取本机内网 IPv4 地址。
     * <p>
     * 遍历所有非回环、非虚拟、已启用的网络接口，取第一个 IPv4 地址。
     * 若获取失败或无有效地址，则回退为 {@code 127.0.0.1}。
     *
     * @return 内网 IPv4 地址
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                // 跳过回环、未启用、虚拟接口
                if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return LOCALHOST_IP;
    }

    /**
     * 判断字符串是否为有效的 IP 值（非 null、非空串且非 unknown 占位值）。
     *
     * @param ip 待校验的 IP 字符串
     * @return 有效返回 {@code true}
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 判断 IP 是否为内网地址（RFC1918 私有地址段或 IPv4 回环地址）。
     *
     * @param ip 待校验的 IP 字符串
     * @return 属于内网地址返回 {@code true}
     */
    private static boolean isInternalIp(String ip) {
        if (ip == null) {
            return false;
        }
        return INTERNAL_IP_SEGMENTS.stream().anyMatch(ip::startsWith) || LOCALHOST_IP.equals(ip);
    }

}
