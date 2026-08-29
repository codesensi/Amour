/**
 * 后台登录守卫 —— 渲染前同步拦截（普通脚本，非 module）。
 *
 * ES Module 均为 defer 语义（DOM 解析完后执行），无法在首帧渲染前完成跳转，
 * 因此快速拦截（无 token 立即跳登录页）在此同步脚本中完成；
 * token 过期的完整校验在 /assets/admin/main.js（module）中进行。
 *
 * 本文件与 common/auth.js 的登录态结构对齐（ADMIN_AUTH：accessToken/expiresAt），
 * 修改存储结构时两处需同步调整。
 */
(function () {
  'use strict';

  var token = null;
  var raw = localStorage.getItem('ADMIN_AUTH');
  if (raw) {
    try {
      token = JSON.parse(raw).accessToken;
    } catch (e) {
      // 数据损坏按未登录处理
    }
  }
  if (!token) {
    location.replace('/admin/login.html');
    return;
  }

  // views 是 pjax 片段页（无外壳脚本，首屏加载遮罩无人关闭），
  // 已登录用户直接访问/刷新片段页时重定向到外壳并携带目标页参数，
  // 由 main.js 读取 ?view= 后经 pjax 进入目标页，保证刷新/分享链接停留原页
  if (/^\/admin\/views\//.test(location.pathname)) {
    location.replace('/admin/index.html?view=' + encodeURIComponent(location.pathname));
  }
})();
