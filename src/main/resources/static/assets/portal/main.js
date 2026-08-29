/**
 * 门户 ESM 入口 —— 装配门户侧的局部刷新（pjax）与页面模块调度。
 *
 * 依赖关系：common/pjax.js（局部刷新核心）、common/config.js（项目配置）
 * 与 portal/js/portal.js（门户业务）均为 ES Module，经本入口统一加载；
 * 页面上仅保留 toastr 等第三方库的普通脚本。
 *
 * 页面模块约定：/portal/<name>.html 的模块为 js/pages/<name>.js（导出 init()），
 * 在 PAGE_MODULES 清单中登记后，整页加载与 pjax 局部注入后均会调度执行。
 */
import { initPjax } from '/assets/common/pjax.js';
import { initPortalPage } from './js/portal.js';

/** 由页面地址推导页面模块名：/portal/about.html -> about；其余返回 null */
function pageModuleName(url) {
  const path = new URL(url, location.href).pathname;
  const m = path.match(/^\/portal\/([a-z-]+)\.html$/);
  return m ? m[1] : null;
}

/**
 * 页面模块清单：键为 /portal/<name>.html 的页面名，值为动态 import 加载器。
 * 清单外的页面不发起模块请求（无模块属正常路径，避免 404 控制台报错）；
 * 新页面需要页面级逻辑时在此登记即可。
 */
const PAGE_MODULES = {
  'about': function () { return import('./js/pages/about.js'); },
  'love-photo': function () { return import('./js/pages/love-photo.js'); }
};

/** 动态加载并执行目标页的页面模块 */
function loadPageModule(url) {
  const name = pageModuleName(url);
  const loader = name && PAGE_MODULES[name];
  if (!loader) {
    return;
  }
  loader()
    .then(function (m) {
      if (typeof m.init === 'function') {
        m.init();
      }
    })
    .catch(function (e) {
      console.error('页面模块加载失败：' + name, e);
    });
}

initPjax({
  // 门户内容区：Thymeleaf 外壳中的局部刷新容器
  container: '#pjax-container',

  // 标题：无条件采用目标页服务器渲染的副标题（首页为空串也赋值，不能保留旧标题），
  // "站点名 — 副标题" 的完整组装由 applySiteConfig 在 onPageReady 中完成
  buildTitle: function (title) {
    return title;
  },

  // 拦截范围：站内页面链接；后台管理走完整加载，静态资源不拦截
  match: function (url) {
    if (/\/admin(\/|$)/.test(url.pathname)) return false;
    return !/\.(png|jpe?g|gif|svg|ico|css|js|zip|pdf|mp4|webp|ttf|woff2?)(\?|$)/i.test(url.pathname);
  },

  // 换页后重新初始化门户页面逻辑并调度目标页模块
  onPageReady: function () {
    initPortalPage();
    loadPageModule(location.href);
  }
});

// 整页加载：module 为 defer 语义，执行时 DOM 已就绪，直接初始化当前页
initPortalPage();
loadPageModule(location.href);
