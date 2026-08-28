package cn.codesensi.amour;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用上下文启动冒烟测试。
 * <p>
 * 验证 Spring 容器能够正常加载（数据源、缓存、定时等基础设施装配无误）。
 */
@SpringBootTest
class AmourApplicationTests {

    /**
     * 验证应用上下文可正常加载。
     */
    @Test
    void contextLoads() {
    }

}
