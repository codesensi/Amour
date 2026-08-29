/**
 * layui 模块加载器（ES Module，两端通用）—— layui.use 的 Promise 化包装。
 *
 * 页面通常已以普通脚本加载 layui.js（全模块一体构建）；
 * 若页面遗漏引入（例如浏览器缓存了旧版外壳），本加载器会按需动态注入
 * layui.js 与样式表并等待就绪（自愈），之后组件内部通过本加载器惰性获取
 * layer/form/laypage 等模块实例，调用方以 await/then 使用，无需关心
 * layui.use 的回调时序。
 *
 * 用法：
 *   const [layer, laypage] = await loadLayui('layer', 'laypage');
 */

/** layui.js 的固定路径（与登录页/后台外壳/门户外壳的引入保持一致） */
const LAYUI_JS = '/assets/layui/2.13.9/layui.js';

/** layui 基础样式目录（两端共用）。外壳经 resourceUrlProvider 引入的是内容指纹 URL，
 *  就位检查须按目录匹配而非精确文件名：否则指纹版判定不到会重复注入一份无指纹
 *  layui.css 追加在文档末尾，把门户/管理端的定制样式全部盖回 layui 默认形态。 */
const LAYUI_CSS_DIR = '/assets/layui/2.13.9/css/';

/** 管理端专属样式：仅管理端路径下自愈注入，避免污染门户布局 */
const ADMIN_CSS = '/assets/admin/css/admin.css';

/** 动态注入的加载 Promise 缓存：多组件并发调用时只注入一次 */
let layuiReady = null;

/**
 * 确保样式表就位（幂等）：浏览器缓存旧版外壳时自动补齐缺失的样式引用。
 * 管理端 admin.css 按路径区分注入——门户页面不引入管理端样式，
 * 其中的 .row 网格等价实现会破坏门户 content.css 的居中布局。
 */
function ensureStyles() {
  const paths = location.pathname.indexOf('/admin') === 0
    ? [LAYUI_CSS_DIR + 'layui.css', ADMIN_CSS]
    : [LAYUI_CSS_DIR + 'layui.css'];
  paths.forEach(function (href) {
    // 按目录匹配：指纹文件名（layui-<hash>.css）不影响就位判定
    if (!document.querySelector('link[href*="' + LAYUI_CSS_DIR + '"]')) {
      var link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = href;
      document.head.appendChild(link);
    }
  });
}

/**
 * 确保 window.layui 就绪：已存在直接通过；缺失时动态注入 layui.js 并等待加载完成。
 * @returns {Promise<void>}
 */
function ensureLayui() {
  if (window.layui) {
    return Promise.resolve();
  }
  if (!layuiReady) {
    layuiReady = new Promise(function (resolve, reject) {
      var script = document.createElement('script');
      script.src = LAYUI_JS;
      script.onload = function () {
        if (window.layui) {
          resolve();
        } else {
          reject(new Error('layui.js 加载完成但全局 layui 未就绪'));
        }
      };
      script.onerror = function () {
        // 失败时清空缓存，允许后续重试
        layuiReady = null;
        reject(new Error('layui 未加载：动态引入 ' + LAYUI_JS + ' 失败'));
      };
      document.head.appendChild(script);
    });
  }
  return layuiReady;
}

/**
 * 加载指定的 layui 模块。
 * @param {...string} names 模块名（如 'layer'、'form'、'laypage'、'flow'）
 * @returns {Promise<Array>} 按传入顺序返回模块实例数组
 */
export function loadLayui(...names) {
  // 样式与脚本都做自愈：浏览器缓存旧外壳时自动补齐缺失引用
  ensureStyles();
  return ensureLayui().then(function () {
    return new Promise(function (resolve) {
      window.layui.use(names, function () {
        resolve(names.map(function (n) { return window.layui[n]; }));
      });
    });
  });
}
