package cn.codesensi.amour.config;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex 审计字段监听器注册。
 * <p>
 * Flex 1.11.8 的监听器分发（{@code TableInfo.invokeOnInsertListener}）仅回调两个来源：
 * {@code @Table(onInsert/onUpdate)} 注解声明的监听器类，以及经
 * {@link FlexGlobalConfig#registerInsertListener} 注册的全局监听器；
 * 实体自身实现监听器接口<b>不会</b>被回调。故将 {@link BaseEntity} 以基类为键全局注册，
 * 注册键与实体类满足 {@code isAssignableFrom} 即命中，覆盖所有继承 BaseEntity 的业务实体，
 * 插入/更新时自动填充 {@code creator}/{@code updater} 审计字段。
 *
 * @since 1.0
 */
@Configuration
public class FlexAuditListenerConfig implements MyBatisFlexCustomizer {

    /**
     * 注册审计字段监听器（自动装配 afterPropertiesSet 阶段执行，先于任何业务插入，
     * 确保 TableInfo 的监听器缓存构建前注册生效）。
     *
     * @param globalConfig Flex 全局配置
     */
    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        globalConfig.registerInsertListener(new BaseEntity(), BaseEntity.class);
        globalConfig.registerUpdateListener(new BaseEntity(), BaseEntity.class);
    }
}
