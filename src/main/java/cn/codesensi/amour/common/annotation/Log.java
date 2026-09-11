package cn.codesensi.amour.common.annotation;

import cn.codesensi.amour.common.enums.LogTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解 —— 标注在 Controller 方法上，声明该方法需要将操作日志写入 {@code sys_log} 表。
 * <p>
 * 由 {@code SysLogAspect} 切面拦截采集：登录人、请求地址、IP 归属地、耗时、
 * 请求参数（脱敏）与响应结果等信息经异步线程落库；
 * 未标注该注解的接口不产生入库日志（HTTP 报文级诊断由 Logbook 负责，两者互不影响）。
 *
 * @author codesensi
 * @since 1.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /**
     * 操作模块，如"用户管理"
     */
    String module();

    /**
     * 操作描述，如"新增用户"
     */
    String operation();

    /**
     * 日志类型
     */
    LogTypeEnum type();

    /**
     * 是否记录请求参数（保存于 sys_log.param）
     */
    boolean saveParam() default true;

    /**
     * 是否记录响应结果（保存于 sys_log.result）
     */
    boolean saveResult() default true;
}
