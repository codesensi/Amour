package cn.codesensi.amour.common.util;

import cn.hutool.core.lang.Validator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.*;
import java.util.Enumeration;

/**
 * IP 地址工具类 —— 从请求中解析真实客户端 IP，以及获取本机 IPv4 地址。
 * <p>
 * 解析时按优先级依次读取常见代理转发头（X-Forwarded-For 等），
 * 均未命中时回退到 {@code RemoteAddr}。
 */
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
     * IPv6 本地回环地址的完整展开形式。
     */
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 多级代理下 X-Forwarded-For 头中多个 IP 之间的分隔符。
     */
    private static final String SEPARATOR = ",";

    /**
     * 本机 IPv4 地址缓存（网络接口遍历较重，解析一次后进程内复用）。
     */
    private static volatile String localIpCache;

    /**
     * 获取客户端IP（从当前线程绑定的请求中解析）。
     * <p>
     * 非 Web 请求线程中调用（无请求上下文）时返回 {@code unknown}。
     *
     * @return IP地址
     */
    public static String getIpAddr() {
        ServletRequestAttributes attributes = ServletUtil.getRequestAttributes();
        return attributes == null ? UNKNOWN : getIpAddr(attributes.getRequest());
    }

    /**
     * 获取真实客户端IP（推荐使用）。
     * <p>
     * 解析优先级：
     * <ol>
     *   <li>{@code X-Forwarded-For}：多级代理下<b>从右向左</b>跳过内网（可信代理）IP，
     *       取第一个非内网 IP——最右侧的条目由最靠近应用的可信代理追加，无法被客户端伪造，
     *       可有效防止通过伪造请求头冒充来源 IP；全为内网时取最右侧的一个；</li>
     *   <li>其他常见代理头：{@code X-Real-IP}、{@code Proxy-Client-IP}、{@code WL-Proxy-Client-IP}；</li>
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

        // 1. 优先检查 X-Forwarded-For（处理多级代理）。
        //    从右向左遍历并跳过内网 IP：最右侧的条目由最靠近应用的可信代理追加，不可被客户端伪造，
        //    从左向右取"第一个非内网 IP"会被客户端伪造的头部内容欺骗。
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            String[] ips = ip.split(SEPARATOR);
            for (int i = ips.length - 1; i >= 0; i--) {
                String trimIp = ips[i].trim();
                if (isValidIp(trimIp) && !isInternalIp(trimIp)) {
                    return trimIp;
                }
            }
            // 全为内网 IP（纯内网部署）时取最右侧的一个；空段等无效值不返回，继续尝试其他代理头
            String last = ips[ips.length - 1].trim();
            if (isValidIp(last)) {
                return last;
            }
        }

        // 2. 检查其他常见的代理头（HTTP_X_FORWARDED_FOR 等为 PHP 风格命名，Servlet 环境下取不到值，已移除）
        String[] headers = {"X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : headers) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return ip;
            }
        }

        // 3. 最后使用 RemoteAddr 兜底，IPv6 回环地址归一化为 IPv4 回环
        return normalizeLoopback(request.getRemoteAddr());
    }

    /**
     * 获取本机 IPv4 地址。
     * <p>
     * 遍历所有非回环、已启用的网络接口，取第一个 IPv4 地址——结果取决于接口枚举顺序，
     * 多网卡（如 Docker、VPN）环境下可能是虚拟网卡的地址，云主机上还可能是公网地址。
     * 结果解析一次后进程内缓存（网络接口遍历较重）；若获取失败或无有效地址，则回退为 {@code 127.0.0.1}。
     *
     * @return 本机 IPv4 地址
     */
    public static String getLocalIp() {
        String cached = localIpCache;
        if (cached != null) {
            return cached;
        }
        String result = LOCALHOST_IP;
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
                        result = addr.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        localIpCache = result;
        return result;
    }

    /**
     * 判断字符串是否为合法的 IP 地址（IPv4 或 IPv6）。
     * <p>
     * 占位值 {@code unknown} 与空串均视为无效。
     *
     * @param ip 待校验的 IP 字符串
     * @return 合法返回 {@code true}
     */
    private static boolean isValidIp(String ip) {
        return ip != null && (Validator.isIpv4(ip) || Validator.isIpv6(ip));
    }

    /**
     * 判断 IP 是否为内网地址。
     * <p>
     * 基于解析后的 {@link InetAddress} 判断，覆盖：IPv4 回环与 RFC1918 私有段
     * （{@code isSiteLocalAddress}）、IPv4-mapped IPv6（如 {@code ::ffff:10.0.0.1}，JDK 会解析
     * 为 {@link Inet4Address}）、IPv6 回环与链路本地地址（fe80::/10），
     * 以及 JDK 未识别的 IPv6 ULA 私有段（fc00::/7）。
     *
     * @param ip 待校验的 IP 字符串
     * @return 属于内网地址返回 {@code true}
     */
    private static boolean isInternalIp(String ip) {
        InetAddress addr = parseIp(ip);
        if (addr == null) {
            return false;
        }
        if (addr.isLoopbackAddress() || addr.isSiteLocalAddress() || addr.isLinkLocalAddress()) {
            return true;
        }
        // 补充 JDK 未识别的 IPv6 ULA 私有段（fc00::/7）
        if (addr instanceof Inet6Address inet6Addr) {
            byte[] bytes = inet6Addr.getAddress();
            return (bytes[0] & 0xfe) == 0xfc;
        }
        return false;
    }

    /**
     * 将 IP 字符串解析为 {@link InetAddress}。
     * <p>
     * 入参应为已通过 {@link #isValidIp(String)} 校验的 IP 字面量，不会触发 DNS 解析。
     *
     * @param ip 待解析的 IP 字符串
     * @return 解析后的 InetAddress；入参非法时返回 {@code null}
     */
    private static InetAddress parseIp(String ip) {
        if (ip == null) {
            return null;
        }
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * 归一化 IPv6 回环地址为 IPv4 回环（{@code 0:0:0:0:0:0:0:1} 与 {@code ::1} 均归一化为 {@code 127.0.0.1}）。
     *
     * @param ip 待归一化的 IP 字符串
     * @return 归一化后的 IP
     */
    private static String normalizeLoopback(String ip) {
        return (LOCALHOST_IPV6.equals(ip) || "::1".equals(ip)) ? LOCALHOST_IP : ip;
    }

}
