/**
 * 轻量消息提示组件（ES Module，零依赖）—— 门户用。
 *
 * 替代 toastr（其 UMD 版本依赖 jQuery，门户为纯原生 JS 不引入 jQuery），
 * 视觉对齐 toastr：右上角堆叠、类型色条、淡入淡出、到时自动消失。
 *
 * 用法（与 toastr 调用形态一致）：
 *   import { toast } from '/assets/common/toast.js';
 *   toast.success('留言提交成功！', 'Like_Girl');
 *   toast.warning('请填写QQ号码！', 'Like_Girl');
 *   toast.error('加载失败');            // 标题可省略
 *
 * message 支持内联 HTML（与 toastr 默认行为一致，调用方自行保证内容安全）。
 */

/** 各类型的色条颜色 */
const TYPES = {
  success: '#16b777',
  warning: '#f0ad4e',
  error: '#d9534f',
  info: '#68b7dd'
};

/** 停留时长（毫秒），对齐后台 toastr 的 timeOut 配置 */
const DURATION = 5000;

/** 提示容器与样式的单例标记 */
let container = null;
let styleInjected = false;

/** 注入组件样式（类名 am- 前缀，归口组件自身，页面无需自带样式） */
function injectStyle() {
  if (styleInjected) return;
  styleInjected = true;
  const style = document.createElement('style');
  style.textContent =
    '.am-toast-container{position:fixed;top:12px;right:12px;z-index:99999;display:flex;' +
    'flex-direction:column;gap:10px;pointer-events:none;}' +
    '.am-toast{pointer-events:auto;min-width:260px;max-width:360px;background:#fff;' +
    'border-radius:6px;box-shadow:0 4px 16px rgba(0,0,0,.18);border-left:4px solid #68b7dd;' +
    'overflow:hidden;opacity:0;transform:translateX(20px);' +
    'transition:opacity .3s ease,transform .3s ease;}' +
    '.am-toast.am-toast-show{opacity:1;transform:translateX(0);}' +
    '.am-toast-body{padding:12px 14px;font-size:14px;line-height:1.6;color:#333;}' +
    '.am-toast-title{font-weight:600;margin-bottom:2px;}';
  document.head.appendChild(style);
}

/** 获取（惰性创建）右上角提示容器 */
function ensureContainer() {
  if (!container) {
    container = document.createElement('div');
    container.className = 'am-toast-container';
    document.body.appendChild(container);
  }
  return container;
}

/** 弹出一条提示：淡入 -> 停留 -> 淡出并移除 */
function show(type, message, title) {
  if (!document.body) {
    return;
  }
  injectStyle();
  const item = document.createElement('div');
  item.className = 'am-toast';
  item.style.borderLeftColor = TYPES[type] || TYPES.info;
  item.innerHTML =
    '<div class="am-toast-body">' +
    (title ? '<div class="am-toast-title"></div>' : '') +
    '<div class="am-toast-msg"></div>' +
    '</div>';
  if (title) {
    item.querySelector('.am-toast-title').textContent = title;
  }
  // 消息支持内联 HTML（含 <br/> 换行），与 toastr 默认行为一致
  item.querySelector('.am-toast-msg').innerHTML = message;
  ensureContainer().appendChild(item);

  // 下一帧再加 show 类，保证淡入过渡生效
  requestAnimationFrame(function () {
    item.classList.add('am-toast-show');
  });
  setTimeout(function () {
    item.classList.remove('am-toast-show');
    setTimeout(function () {
      item.remove();
    }, 300);
  }, DURATION);
}

export const toast = {
  success: function (message, title) { show('success', message, title); },
  warning: function (message, title) { show('warning', message, title); },
  error: function (message, title) { show('error', message, title); },
  info: function (message, title) { show('info', message, title); }
};
