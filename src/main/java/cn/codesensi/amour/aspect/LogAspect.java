package cn.codesensi.amour.aspect;

import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.Result;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.enums.SuccessEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.SystemException;
import cn.codesensi.amour.common.util.Ip2regionUtil;
import cn.codesensi.amour.common.util.IpUtil;
import cn.codesensi.amour.common.util.LoginUserUtil;
import cn.codesensi.amour.common.util.ServletUtil;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.model.entity.SysLog;
import cn.codesensi.amour.model.request.LoginRequest;
import cn.codesensi.amour.service.SysConfigService;
import cn.codesensi.amour.service.SysLogService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.autoconfigure.LogbookProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志切面 —— 拦截标注了 {@link Log} 注解的接口，组装操作日志并异步写入 {@code sys_log} 表。
 * <p>
 * 采集与入库分工：日志实体在<b>请求线程内</b>组装完成（traceId、登录人、IP 等均取自请求上下文），
 * 组装完成后交由 {@code SysLogService#record} 异步写入，规避跨线程上下文丢失问题；
 * 入库失败仅告警，绝不影响业务。
 * <p>
 * 采集口径：
 * <ul>
 *     <li>请求参数：查询字符串 + 方法入参（JSON 序列化，覆盖路径变量、JSON/表单体、文件上传），脱敏后存储，超长时降级为截断标记对象；</li>
 *     <li>响应结果：{@link Result} 整体序列化后存储（超长时降级为截断标记对象）；异常时按全局异常处理器的转换口径构造失败响应存储；</li>
 *     <li>登录人：优先取当前登录用户，未登录（如登录接口）时从请求参数中提取用户名。</li>
 * </ul>
 *
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * Logbook 的 JSON 字段脱敏过滤器 —— 与 Logbook 文件日志共用同一份 obfuscate 配置与实现
     */
    private final BodyFilter bodyFilter;

    /**
     * param/result 文本最大存储长度，取自 Logbook 的 write.max-body-size 配置
     */
    private final int maxTextLength;

    private final SysLogService sysLogService;

    /**
     * 系统配置服务 —— 读取代理头可信开关（trust-proxy-headers），IP 解析口径与限流切面保持一致
     */
    private final SysConfigService sysConfigService;

    /**
     * 注入 Logbook 的 JSON 字段脱敏过滤器与截断上限 —— 与 Logbook 文件日志共用同一份 yml 配置，一处配置两处生效。
     *
     * @param bodyFilter        Logbook 的 JSON 字段脱敏过滤器（jsonBodyFieldsFilter Bean）
     * @param logbookProperties Logbook 配置属性（write 板块）
     * @param sysLogService     操作日志服务
     * @param sysConfigService  系统配置服务（代理头可信开关）
     */
    public LogAspect(@Qualifier("jsonBodyFieldsFilter") BodyFilter bodyFilter,
                     LogbookProperties logbookProperties,
                     SysLogService sysLogService,
                     SysConfigService sysConfigService) {
        this.bodyFilter = bodyFilter;
        this.maxTextLength = logbookProperties.getWrite().getMaxBodySize();
        this.sysLogService = sysLogService;
        this.sysConfigService = sysConfigService;
    }

    /**
     * 环绕标注了 {@code @Log} 的方法：执行业务并采集操作日志。
     * <p>
     * 正常返回时读取 {@link Result} 判定成败；抛出异常时记录失败原因后原样重抛，
     * 由全局异常处理器统一转换为响应。
     * <p>
     * 登录人在业务执行<b>前</b>前置采集——登出操作会使会话失效，执行后再取将拿不到；
     * 业务正常返回后若仍未登录（登录场景）则补采一次，此时新会话已建立。
     *
     * @param joinPoint 切点
     * @param log       方法上标注的日志注解
     * @return 业务返回值
     * @throws Throwable 业务异常（原样上抛）
     */
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        long start = System.currentTimeMillis();
        Long userId = LoginUserUtil.getLoginIdOrNull();
        try {
            Object result = joinPoint.proceed();
            if (ObjUtil.isNull(userId)) {
                // 登录场景：执行前未登录，执行后新会话已建立，补采一次
                userId = LoginUserUtil.getLoginIdOrNull();
            }
            save(joinPoint, log, start, result, null, userId);
            return result;
        } catch (Throwable e) {
            save(joinPoint, log, start, null, e, userId);
            throw e;
        }
    }

    /**
     * 组装操作日志实体并异步入库。
     * <p>
     * 全程捕获异常——日志采集/入库的任何失败都不影响业务响应。
     *
     * @param joinPoint 切点
     * @param log       日志注解
     * @param start     方法开始时间戳（毫秒）
     * @param result    业务返回值（异常时为 null）
     * @param e         业务抛出的异常（正常返回时为 null）
     * @param userId    登录人 ID（前置采集，登录场景为补采结果）
     */
    private void save(ProceedingJoinPoint joinPoint, Log log, long start, Object result, Throwable e, Long userId) {
        try {
            SysLog sysLog = new SysLog();
            sysLog.setTraceId(MDC.get(AppConst.TRACE_ID));
            sysLog.setLogType(log.type().getCode());
            sysLog.setModule(log.module());
            sysLog.setOperation(log.operation());
            sysLog.setMethod(buildMethod(joinPoint));

            sysLog.setUserId(userId);
            // 本条日志在异步线程入库，BaseEntity 监听器在日志线程内取不到登录用户（无会话上下文），
            // creator 必须趁请求线程上下文仍在时手动填充，不可删除
            sysLog.setCreator(userId);
            if (ObjUtil.isNull(userId)) {
                // 未登录（如登录接口）：用户名从请求参数中提取
                fillUsernameFromArgs(joinPoint, sysLog);
            }

            // 非 Web 线程触发时无请求上下文,请求维度字段(url/ip/region/param)整体跳过
            HttpServletRequest request = ServletUtil.getRequest();
            if (ObjUtil.isNotNull(request)) {
                sysLog.setUrl(request.getRequestURI());
                // 代理头可信开关与限流切面同口径:不信任代理头时记录连接对端地址
                SysConfig trustSwitch = sysConfigService.oneByKey(ConfigKeyEnum.TRUST_PROXY_HEADERS.getCode());
                boolean trustProxyHeaders = ObjUtil.isNotNull(trustSwitch) && Boolean.parseBoolean(trustSwitch.getConfigValue());
                String ip = IpUtil.getIpAddr(request, trustProxyHeaders);
                sysLog.setIp(ip);
                sysLog.setRegion(Ip2regionUtil.search(ip));
                if (log.saveParam()) {
                    sysLog.setParam(fitJson(buildParam(request, joinPoint), maxTextLength));
                }
            }

            if (ObjUtil.isNotNull(e)) {
                sysLog.setStatus(SuccessEnum.FAIL.getCode());
                sysLog.setMsg(StrUtil.subPre(e.getMessage(), AppConst.MSG_MAX_LENGTH));
                if (log.saveResult()) {
                    // 失败响应与全局异常处理器口径一致:按异常类型构造失败 Result 记录,详情可完整回放响应体
                    sysLog.setResult(fitJson(mask(toJson(buildFailResult(e))), maxTextLength));
                }
            } else {
                // 响应结果与真实响应体保持一致：@ApiResponseBody 会把原始返回值包装为 Result.success(...)
                sysLog.setStatus(SuccessEnum.SUCCESS.getCode());
                if (result instanceof Result<?> r) {
                    sysLog.setMsg(StrUtil.subPre(r.getMsg(), AppConst.MSG_MAX_LENGTH));
                }
                if (log.saveResult()) {
                    Object body = (result instanceof Result<?> r) ? r : Result.success(result);
                    sysLog.setResult(fitJson(mask(toJson(body)), maxTextLength));
                }
            }

            sysLog.setElapsed(System.currentTimeMillis() - start);
            sysLogService.record(sysLog);
        } catch (Exception ex) {
            LogAspect.log.warn("操作日志采集失败：{}", ex.getMessage());
        }
    }

    /**
     * 构建失败响应结果：与全局异常处理器对各类异常的转换口径保持一致。
     * <p>
     * {@code ValidationException}/{@code AuthorizationException} 均为 {@code BusinessException} 子类
     * （默认码 400/403），统一由父类分支透传业务码；{@code SystemException} 透传系统错误码；
     * 兜底口径对齐"未捕获异常不透传内部细节"的原则。
     *
     * @param e 业务抛出的异常
     * @return 失败响应体
     */
    private Result<Void> buildFailResult(Throwable e) {
        if (e instanceof BusinessException be) {
            return Result.error(be.getCode(), be.getMsg());
        }
        if (e instanceof SystemException se) {
            return Result.error(se.getCode(), se.getMsg());
        }
        return Result.systemError("系统繁忙，请稍后重试");
    }

    /**
     * 构建请求参数：查询字符串 + 方法入参（JSON 序列化），统一脱敏。
     * <p>
     * 覆盖全部请求类型：查询参数走 query string，路径变量与请求体经 Spring 绑定后
     * 出现在方法入参中；文件上传参数仅记录文件名与大小，不序列化二进制内容。
     *
     * @param request   HTTP 请求
     * @param joinPoint 切点
     * @return 脱敏后的参数 JSON；无任何参数时返回 {@code null}
     */
    private String buildParam(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        String queryString = request.getQueryString();
        List<Object> params = new ArrayList<>();
        for (Object arg : joinPoint.getArgs()) {
            if (ObjUtil.isNull(arg)) {
                continue;
            }
            if (arg instanceof MultipartFile file) {
                // 文件上传：仅记录元信息，不序列化二进制内容
                params.add(new FileMeta(file.getName(), file.getOriginalFilename(), file.getSize()));
            } else if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof BindingResult) {
                // 排除请求对象、响应对象与校验结果对象
                log.debug("排除请求对象、响应对象与校验结果对象");
            } else {
                params.add(arg);
            }
        }
        if (StrUtil.isBlank(queryString) && CollUtil.isEmpty(params)) {
            return null;
        }
        // 组件为 null 时 Hutool 默认忽略该键,输出 JSON 结构与键序(query → params)保持不变
        return mask(toJson(new ParamLog(StrUtil.blankToDefault(queryString, null),
                CollUtil.isEmpty(params) ? null : params)));
    }

    /**
     * 未登录场景下从请求参数中提取用户名（覆盖登录接口的审计需求）：
     * 登录类请求参数为强类型的 {@link LoginRequest}，直接读取其账号字段，
     * 避免按字段名字符串解析形成的跨类隐式契约。
     *
     * @param joinPoint 切点
     * @param sysLog    待填充的日志实体
     */
    private void fillUsernameFromArgs(ProceedingJoinPoint joinPoint, SysLog sysLog) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof LoginRequest loginRequest) {
                String username = loginRequest.getUsername();
                if (StrUtil.isNotBlank(username)) {
                    sysLog.setUsername(username);
                    return;
                }
            }
        }
    }

    /**
     * 拼接请求类方法：全限定类名#方法名。
     *
     * @param joinPoint 切点
     * @return 形如 cn.codesensi.amour.controller.LoginController#login 的方法标识
     */
    private String buildMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "#" + signature.getName();
    }

    /**
     * 对 JSON 文本做敏感字段脱敏：委托 Logbook 的 JSON 字段过滤器实现（Jackson 解析，支持嵌套与数字值），
     * 替换符取自 Logbook 的 obfuscate.replacement 配置。
     *
     * @param json 原始 JSON 文本
     * @return 脱敏后的 JSON 文本
     */
    private String mask(String json) {
        return bodyFilter.filter(MediaType.APPLICATION_JSON_VALUE, json);
    }

    /**
     * 序列化对象为 JSON 字符串；序列化失败时降级为 toString。
     *
     * @param obj 待序列化对象
     * @return JSON 字符串
     */
    private String toJson(Object obj) {
        try {
            return JSONUtil.toJsonStr(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }

    /**
     * 将 JSON 文本适配到最大存储长度：未超长原样返回；超长时降级为结构化标记对象
     * {@code {"truncated":true,"originLength":N,"preview":"前 maxLength 字符"}}，
     * 保证列中存储的始终是合法 JSON，便于前端解析展示。
     * <p>
     * 调用前应先完成脱敏——preview 中的内容已不含敏感明文。
     *
     * @param json      原始 JSON 文本
     * @param maxLength 最大存储长度
     * @return 适配后的 JSON 文本；入参为 null 时返回 null
     */
    private String fitJson(String json, int maxLength) {
        if (StrUtil.length(json) <= maxLength) {
            return json;
        }
        return JSONUtil.toJsonStr(new TruncatedJson(true, json.length(), StrUtil.subPre(json, maxLength)));
    }

    /**
     * 日志参数的顶层存储结构 —— JSON 键名即存储格式（query/params），由组件名生成；
     * 组件为 null 时由 Hutool 默认忽略，不输出对应键。
     */
    private record ParamLog(String query, List<Object> params) {
    }

    /**
     * 文件上传参数的日志元信息 —— JSON 键名即存储格式（name/originalFilename/size），由组件名生成
     */
    private record FileMeta(String name, String originalFilename, long size) {
    }

    /**
     * 截断标记对象 —— JSON 键名即存储格式，由组件名生成，前端解析依赖同名键
     */
    private record TruncatedJson(boolean truncated, int originLength, String preview) {
    }
}
