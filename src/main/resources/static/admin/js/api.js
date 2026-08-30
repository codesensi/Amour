/**
 * 统一请求层 —— 全站所有数据接口的唯一出口
 *
 * - 业务代码只面向路由表 key 与统一响应格式编程，不感知 mock 或真实后端
 * - 统一响应契约（与后端 Result 一致）：{success, code, msg, data, timestamp}
 * - 成功（code === successCode）回调直接返回 data；失败统一 layer.msg 提示
 *
 * 出口：
 *   api.request(key, data)              通用请求（GET 查询参数 / POST JSON 请求体）
 *   api.url(key)                        取接口地址（供只接受 url 的场景）
 *   api.table(key, extra)               生成 layui table 配置（url + parseData + token）
 *   api.getToken/setToken/clearToken    token 本地存取（集中管理）
 *   api.headers()                       真实模式统一请求头
 */
layui.define(['jquery', 'layer'], function (exports) {
    var $ = layui.$,
        layer = layui.layer,
        cfg = window.APP_API;

    /** token 本地存储键（与后端 LoginResponse.accessToken 语义对应） */
    var TOKEN_KEY = 'accessToken';

    /**
     * 接口路由表
     * mock：本地模拟文件路径（统一 Result 格式）
     * real：真实后端接口路径（与后端 Controller 对齐）
     */
    var routes = {
        init:        {mock: 'api/init.json',        real: '/admin/init',         method: 'get'},
        clearCache:  {mock: 'api/clear.json',       real: '/cache/clear',        method: 'get'},
        userPage:    {mock: 'api/table.json',       real: '/admin/user/page',    method: 'get'},
        menuTree:    {mock: 'api/menus.json',       real: '/admin/menu/tree',    method: 'get'},
        tableSelect: {mock: 'api/tableSelect.json', real: '/admin/user/list',    method: 'get'},
        login:       {mock: 'api/login.json',       real: '/auth/login',         method: 'post'},
        logout:      {mock: 'api/logout.json',      real: '/auth/logout',        method: 'post'},
        captcha:     {mock: 'api/captcha.json',     real: '/captcha',            method: 'get'},
        upload:      {mock: 'api/upload.json',      real: '/upload',             method: 'post'},
        passwordChange: {mock: 'api/success.json',  real: '/user/password',      method: 'post'},
        profileSave:    {mock: 'api/success.json',  real: '/user/profile',       method: 'post'},
        statistics:     {mock: 'api/statistics.json', real: '/admin/dashboard',  method: 'get'}
    };

    /** token 本地存取 —— 全站统一入口 */
    function getToken() {
        return localStorage.getItem(TOKEN_KEY) || '';
    }

    function setToken(value) {
        localStorage.setItem(TOKEN_KEY, value);
    }

    function clearToken() {
        localStorage.removeItem(TOKEN_KEY);
    }

    /** 真实模式统一请求头（mock 模式不携带），形如 Authorization: Bearer {token} */
    function headers() {
        var h = {};
        if (!cfg.mock && getToken()) {
            h[cfg.tokenName] = cfg.tokenPrefix ? cfg.tokenPrefix + ' ' + getToken() : getToken();
        }
        return h;
    }

    /** 统一 Result 校验与解包：成功 resolve data，失败统一提示并 reject */
    function settle(deferred, res) {
        if (res && res.code === cfg.successCode) {
            deferred.resolve(res.data, res);
        } else {
            layer.msg((res && res.msg) || '业务处理失败', {icon: 2});
            deferred.reject(res);
        }
    }

    /** 通用请求 */
    function request(key, data) {
        var route = routes[key];
        var deferred = $.Deferred();
        if (!route) {
            layer.msg('未注册的接口：' + key, {icon: 2});
            return deferred.reject().promise();
        }
        if (cfg.mock) {
            // mock 模式：强制 GET 本地 json（静态容器不支持 POST）
            $.ajax({
                url: route.mock,
                type: 'get',
                dataType: 'json',
                timeout: cfg.timeout
            }).done(function (res) {
                settle(deferred, res);
            }).fail(function (xhr) {
                layer.msg('模拟接口异常：' + route.mock, {icon: 2});
                deferred.reject(xhr);
            });
        } else {
            $.ajax({
                url: cfg.baseUrl + route.real,
                type: route.method,
                data: route.method === 'get' ? data : JSON.stringify(data),
                contentType: route.method === 'get' ? undefined : 'application/json;charset=UTF-8',
                dataType: 'json',
                headers: headers(),
                timeout: cfg.timeout
            }).done(function (res) {
                settle(deferred, res);
            }).fail(function (xhr) {
                if (xhr.status === 401) {
                    layer.msg('登录已失效，请重新登录', {icon: 2});
                    clearToken();
                    setTimeout(function () {
                        window.location.href = 'login.html';
                    }, 1500);
                } else {
                    layer.msg('接口异常：' + route.real + '（' + xhr.status + '）', {icon: 2});
                }
                deferred.reject(xhr);
            });
        }
        return deferred.promise();
    }

    /** 取接口地址（供 miniAdmin.iniUrl 等只接受 url 的场景） */
    function url(key) {
        var route = routes[key];
        return cfg.mock ? route.mock : cfg.baseUrl + route.real;
    }

    /**
     * 生成 layui table 配置
     * 统一响应 Result{data:{list, count}} → layui{code:0, msg, count, data}
     */
    function table(key, extra) {
        var options = $.extend({
            url: url(key),
            // 分页参数名，接入后端时按后端分页 DTO 调整
            request: {pageName: 'page', limitName: 'limit'},
            parseData: function (res) {
                var ok = res && res.code === cfg.successCode;
                return {
                    code: ok ? 0 : (res ? res.code : -1),
                    msg: res ? res.msg : '响应解析失败',
                    count: ok ? ((res.data && res.data.count) || 0) : 0,
                    data: ok ? ((res.data && res.data.list) || []) : []
                };
            }
        }, extra || {});
        if (!cfg.mock) {
            options.headers = headers();
        }
        return options;
    }

    exports('api', {
        request: request,
        url: url,
        table: table,
        routes: routes,
        getToken: getToken,
        setToken: setToken,
        clearToken: clearToken,
        headers: headers
    });
});
