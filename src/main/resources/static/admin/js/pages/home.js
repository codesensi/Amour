import { toast } from '/assets/common/toast.js';
/**
 * 后台首页页面模块（由 pjax 页面调度加载）。
 */
export function init() {
    // 登录成功欢迎提示（仅从登录页跳转过来时展示一次）
    if (sessionStorage.getItem('ADMIN_LOGIN_WELCOME') === '1') {
        sessionStorage.removeItem('ADMIN_LOGIN_WELCOME');
        toast.success('登录成功！欢迎回来~');
    }
}
