/**
 * 登录态管理 —— 登录 / 退出 / 登录态检查
 *
 * - mock 模式：login 读取本地 login.json 模拟响应（结构与后端 LoginResponse 一致）
 * - 真实模式：调用后端 /auth/login、/auth/logout，token 由统一请求层持久化
 */
layui.define(['jquery', 'api'], function (exports) {
    var $ = layui.$,
        api = layui.api,
        cfg = window.APP_API;

    var auth = {

        /**
         * 登录：成功后持久化 token
         * @param username 账号
         * @param password 密码
         * @param extra    额外字段（真实模式的验证码：{captchaKey, captchaValue}）
         */
        login: function (username, password, extra) {
            var data = $.extend({username: username, password: password}, extra || {});
            return api.request('login', data).then(function (res) {
                // 后端 LoginResponse：{accessToken, expires, tokenName, tokenPrefix}
                if (res && res.accessToken) {
                    api.setToken(res.accessToken);
                }
            });
        },

        /** 退出：双模式均清理本地登录态，真实模式同时通知后端 */
        logout: function () {
            var deferred = $.Deferred();
            api.request('logout').always(function () {
                api.clearToken();
                deferred.resolve();
            });
            return deferred.promise();
        },

        /** 登录态检查：mock 模式默认通过；真实模式有 token 即视为已登录 */
        check: function () {
            return cfg.mock || !!api.getToken();
        },

        /** 跳转登录页 */
        redirectLogin: function () {
            window.location.href = 'login.html';
        }
    };

    exports('auth', auth);
});
