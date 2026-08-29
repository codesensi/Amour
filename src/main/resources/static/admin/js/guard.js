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
  }
})();
