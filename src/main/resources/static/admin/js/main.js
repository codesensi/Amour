/**
 * 后台管理 ESM 入口 —— �一的模块加载点，按职责装配四块能力：
 *
 * 1. 登录完整校验：guard.js（同步脚本）已拦截无 token 的访问，
 *    此处校验 token 过期，过期则清除登录态并回到登录页；
 * 2. 请求守卫：同源请求统一携带 accessToken，401 统一处理；
 * 3. 外壳初始化：用户名、站点名、头像、退出登录（仅整页加载执行一次，
 *    pjax 局部刷新不替换外壳，无需重复执行）；
 * 4. pjax 与页面模块调度：局部刷新注入内容区后，按目标地址动态加载
 *    对应的页面模块（admin/js/pages/<name>.js）并执行其 init()。
 *
 * 页面模块约定：admin/js/pages/<name>.js 导出 init(options)，其中 <name>
 * 由页面地址推导（views/set.html -> set；index.html -> home）。
 */
import { getAuth, isLoggedIn, clearAuth, redirectToLogin, setupAjaxGuard } from '/assets/common/auth.js';
import { getConfig, qqAvatar } from '/assets/common/config.js';
import { initPjax } from '/assets/common/pjax.js';
import { initAdminDataTables, destroyAdminDataTables } from './datatable-init.js';

/** 后台页面依赖的全局 jQuery（主题与 vendor 脚本以普通脚本形式加载） */
const $ = window.jQuery;

/** 全局 toastr（页面上以普通脚本加载，此处显式桥接） */
const toastr = window.toastr;

/* ---------- 登录完整校验 ---------- */
if (!isLoggedIn()) {
  redirectToLogin();
}

/* ---------- 请求守卫：token 注入 + 401 统一处理 ---------- */
setupAjaxGuard();

/* ---------- toastr 全局默认配置（原各页面重复配置归口此处） ---------- */
toastr.options = {
  closeButton: true,
  debug: false,
  newestOnTop: false,
  progressBar: true,
  rtl: false,
  positionClass: 'toast-top-right',
  preventDuplicates: false,
  onclick: null,
  showDuration: 300,
  hideDuration: 1000,
  timeOut: 5000,
  extendedTimeOut: 1000,
  showEasing: 'swing',
  hideEasing: 'linear',
  showMethod: 'fadeIn',
  hideMethod: 'fadeOut'
};

/* ---------- 页面模块调度 ---------- */

/**
 * 由页面地址推导页面模块名：
 * /admin/views/set.html -> set；/admin/index.html -> home；其余返回 null。
 */
function pageModuleName(url) {
  const path = new URL(url, location.href).pathname;
  const views = path.match(/^\/admin\/views\/([a-z-]+)\.html$/);
  if (views) {
    return views[1];
  }
  if (/^\/admin\/(index\.html)?$/.test(path)) {
    return 'home';
  }
  return null;
}

/** 动态加载并执行目标页的页面模块；无模块的页面静默跳过 */
function loadPageModule(url) {
  const name = pageModuleName(url);
  if (!name) {
    return;
  }
  import('./pages/' + name + '.js')
    .then(function (m) {
      if (typeof m.init === 'function') {
        m.init();
      }
    })
    .catch(function (e) {
      console.error('页面模块加载失败：' + name, e);
    });
}

/* ---------- pjax：局部刷新装配 ---------- */
let siteName = '';
let lastPageTitle = document.title;

initPjax({
  // 后台内容区：顶栏、侧边菜单等外壳保持不动
  container: '.content-page',

  // 拦截范围：/admin/ 目录下的 .html 页面
  match: function (url) {
    return url.pathname.indexOf('/admin/') === 0 && /\.html$/.test(url.pathname);
  },

  // 标题组装：站点名 + 目标页静态标题
  buildTitle: function (pageTitle) {
    lastPageTitle = pageTitle;
    return siteName ? siteName + ' - ' + pageTitle : pageTitle;
  },

  // 交换前销毁旧表格状态
  onBeforeSwap: function (target) {
    destroyAdminDataTables($(target));
  },

  // 注入后：重渲染表格 + 调度目标页模块
  onPageReady: function () {
    initAdminDataTables();
    loadPageModule(location.href);
  }
});

/* ---------- 外壳初始化（仅整页加载执行一次） ---------- */

// 加载动画淡出（原各页内联脚本归口此处；页面无该遮罩时 jQuery 调用为空操作）
$('#Loadanimation').fadeOut(1000);

// 顶栏与侧栏展示当前登录用户名
const auth = getAuth();
$('.js-username').text(auth && auth.username ? auth.username : 'Admin');

// 站点名称：浏览器标签 + 顶栏 Logo + 侧栏用户名均取自站点配置 name
getConfig('name').then(function (name) {
  if (!name) return;
  siteName = name;
  document.title = name + ' - ' + lastPageTitle;
  $('.js-site-name').text(name);
});

// 用户头像：站点配置的男主 QQ -> QQ 头像；加载失败回退占位图，避免破图
getConfig('site.male-qq').then(function (qq) {
  const fallback = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="104" height="104"><rect width="104" height="104" fill="#ff5295"/><text x="52" y="66" font-size="46" fill="#fff" text-anchor="middle" font-family="serif">\u2665</text></svg>');
  $('.js-user-avatar')
    .on('error', function () { $(this).off('error').attr('src', fallback); })
    .attr('src', qqAvatar(qq, 640));
});

// 退出登录：清除本地登录态并回到登录页（后端 /auth/logout 实现后可先请求接口再跳转）
$('.js-logout').on('click', function () {
  clearAuth();
  location.href = '/admin/login.html';
});

/* ---------- 整页加载：初始化表格并执行当前页模块 ---------- */
initAdminDataTables();
loadPageModule(location.href);
