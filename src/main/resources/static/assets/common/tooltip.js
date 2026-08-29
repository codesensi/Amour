/**
 * data-tip 声明式悬浮提示（ES Module，两端通用）—— 基于 layui layer 的 layer.tips。
 *
 * 用法：元素上写 data-tip="提示内容"（可选 data-tip-position="top|bottom"），
 * 悬浮自动弹出、移开自动关闭；事件委托方式实现，pjax 换页后对动态内容同样生效。
 *
 * 页面需已加载 layui.js（本组件经 common/layui.js 惰性等待 layer 就绪）。
 */
import { loadLayui } from './layui.js';

let layerPromise = null;

/** 惰性获取 layer 实例（首次调用时等待 layui 模块就绪） */
function getLayer() {
  if (!layerPromise) {
    layerPromise = loadLayui('layer').then(function (m) { return m[0]; });
  }
  return layerPromise;
}

let currentIndex = null;
let currentEl = null;

/** 关闭当前已弹出的提示 */
function hide() {
  currentEl = null;
  if (currentIndex !== null) {
    const index = currentIndex;
    currentIndex = null;
    getLayer().then(function (layer) { layer.close(index); });
  }
}

/** 初始化全局悬浮提示（事件委托，重复调用安全） */
export function initTooltip() {
  if (initTooltip._bound) {
    return;
  }
  initTooltip._bound = true;

  document.addEventListener('mouseover', function (e) {
    const el = e.target.closest('[data-tip]');
    if (!el || el === currentEl) return;
    hide();
    currentEl = el;
    const position = el.getAttribute('data-tip-position') || 'top';
    getLayer().then(function (layer) {
      // 异步竞态防护：等待 layer 就绪期间鼠标可能已移到别的元素
      if (currentEl !== el) return;
      // data-tip 原为纯文本展示，此处转义后传入，防止内容被当作 HTML
      const text = el.getAttribute('data-tip') || '';
      const escaped = String(text)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
      currentIndex = layer.tips(escaped, el, {
        tips: [position === 'bottom' ? 2 : 1, '#333333'],
        time: 0
      });
    });
  });

  document.addEventListener('mouseout', function (e) {
    if (e.target.closest('[data-tip]')) hide();
  });

  window.addEventListener('scroll', hide);
}
