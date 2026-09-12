package cn.codesensi.amour.common.util;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 当前登录用户工具 —— 统一收敛 StpUtil 的登录态读取语义。
 *
 * @author codesensi
 * @since 1.0
 */
public final class LoginUserUtil {

    /**
     * 线程级登录用户ID持有器:由 ContextTaskDecorator 在任务执行前绑定提交者身份,
     * 使审计字段填充等场景在异步线程中仍能取到登录用户。
     */
    private static final ThreadLocal<Long> LOGIN_ID_HOLDER = new ThreadLocal<>();

    private LoginUserUtil() {
    }

    /**
     * 获取当前登录用户ID（容错版）。
     * <p>
     * 优先取线程绑定的登录用户ID（异步任务经 ContextTaskDecorator 传递的提交者身份），
     * 无绑定时走 Sa-Token 会话；未登录（公开接口）或无会话上下文（异步线程、系统任务等）时
     * 返回 null 而不抛出异常，适用于日志记录、审计字段填充等"尽力而为"的场景；
     * 登录态由拦截器保证的业务接口请直接使用 {@link StpUtil#getLoginIdAsLong()}，
     * 失败时由全局异常处理器转为 401 语义。
     *
     * @return 当前登录用户ID，未登录时为 null
     */
    public static Long getLoginIdOrNull() {
        Long boundId = LOGIN_ID_HOLDER.get();
        if (boundId != null) {
            return boundId;
        }
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 未登录或无 Web 会话上下文（如异步线程）时视为未登录
            return null;
        }
    }

    /**
     * 读取当前线程已绑定的登录用户ID（仅查绑定值，不回源会话），
     * 供 ContextTaskDecorator 在任务结束时还原线程既有状态。
     *
     * @return 绑定的登录用户ID，未绑定时为 null
     */
    public static Long getBoundId() {
        return LOGIN_ID_HOLDER.get();
    }

    /**
     * 绑定当前线程的登录用户ID，传 null 等价于解绑。
     *
     * @param userId 登录用户ID，可为 null（解绑）
     */
    public static void bindLoginId(Long userId) {
        if (userId != null) {
            LOGIN_ID_HOLDER.set(userId);
        } else {
            LOGIN_ID_HOLDER.remove();
        }
    }
}
