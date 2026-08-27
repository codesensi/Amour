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
 * IP地址工具类
 */
@Slf4j
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String SEPARATOR = ",";

    // 内网IP段（用于识别代理服务器）
    private static final Set<String> INTERNAL_IP_SEGMENTS = new HashSet<>(Arrays.asList(
            "10.", "192.168.", "172.16.", "172.17.", "172.18.", "172.19.",
            "172.20.", "172.21.", "172.22.", "172.23.", "172.24.", "172.25.",
            "172.26.", "172.27.", "172.28.", "172.29.", "172.30.", "172.31."
    ));

    /**
     * 获取客户端IP
     *
     * @return IP地址
     */
    public static String getIpAddr() {
        return getIpAddr(ServletUtil.getRequest());
    }

    /**
     * 获取真实客户端IP（推荐使用）
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

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }

    private static boolean isInternalIp(String ip) {
        if (ip == null) {
            return false;
        }
        return INTERNAL_IP_SEGMENTS.stream().anyMatch(ip::startsWith) || LOCALHOST_IP.equals(ip);
    }

}
