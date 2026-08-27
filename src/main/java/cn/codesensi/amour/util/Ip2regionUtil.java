package cn.codesensi.amour.util;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.InvalidConfigException;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.XdbException;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * ip2region 工具类 —— 通过 classpath 下的 xdb 库文件查询 IP 归属地。
 * <p>
 * 以纯静态方式在类加载时把 {@code xdb/ip2region_v4.xdb}、{@code xdb/ip2region_v6.xdb}
 * 加载进内存，无 Spring 容器依赖、天然线程安全；加载失败时降级处理（查询返回「未知」）。
 */
@Slf4j
public class Ip2regionUtil {

    /**
     * ip2region 实例，在类首次被主动使用（如首次调用 {@link #search(String)}）时由
     * {@link #init()} 初始化一次；加载失败时为 null，查询降级返回「未知」。
     * <p>
     * 以 {@code static final} 修饰，由 JVM 类加载时的 {@code <clinit>} 保证可见性与线程安全，
     * 无需额外同步。
     */
    private static final Ip2Region IP_2_REGION = init();

    /**
     * 从 classpath 加载 v4/v6 xdb 库文件并构建 {@link Ip2Region} 实例。
     *
     * @return 构建完成的 {@link Ip2Region}；加载失败时返回 null
     */
    private static Ip2Region init() {
        try (InputStream v4 = new ClassPathResource("xdb/ip2region_v4.xdb").getInputStream();
             InputStream v6 = new ClassPathResource("xdb/ip2region_v6.xdb").getInputStream()) {
            Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(15)
                    .setXdbInputStream(v4)
                    .asV4();
            Config v6Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(15)
                    .setXdbInputStream(v6)
                    .asV6();
            return Ip2Region.create(v4Config, v6Config);
        } catch (IOException | XdbException | InvalidConfigException e) {
            log.warn("ip2region 数据加载失败，查询将降级返回「未知」", e);
            return null;
        }
    }

    /**
     * 查询 IP 对应的地区；未初始化或查询失败时返回「未知」。
     *
     * @param ip 待查询的 IP 地址
     * @return IP 归属地描述；内网地址返回「内网」，失败返回「未知」
     */
    public static String search(String ip) {
        if (IP_2_REGION == null) {
            log.warn("ip2region 未初始化，ip={}", ip);
            return "未知";
        }
        try {
            String search = IP_2_REGION.search(ip);
            if (search.contains("Reserved")) {
                return "内网";
            }
            // 去掉 |0 及 0|
            search = search.replace("|0", "").replace("0|", "");
            // 去掉最后一个 | 及后边的内容
            search = search.substring(0, search.lastIndexOf("|"));
            return search;
        } catch (Exception e) {
            log.warn("ip2region 查询失败，ip={}", ip, e);
            return "未知";
        }
    }

}
