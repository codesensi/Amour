/**
 * 消息提示组件（ES Module，两端通用）—— 基于 layui layer 的 layer.msg。
 *
 * 页面需已加载 layui.js（本组件经 common/layui.js 惰性等待 layer 就绪）。
 * 替代自研 toast 与后台 toastr 的调用形态：message 支持内联 HTML，
 * title 可选，弹出时以「title · message」形式合并展示。
 *
 * 用法：
 *   import { toast } from '/assets/common/toast.js';
 *   toast.success('留言提交成功！', 'Like_Girl');
 *   toast.warning('请填写QQ号码！', 'Like_Girl');
 *   toast.error('加载失败');
 */
import { loadLayui } from './layui.js';

/** 各类型对应的 layer 内置图标（0 为感叹号；info 不带图标走纯文本样式） */
const ICONS = {
  success: 1,
  error: 2,
  warning: 0,
  info: -1
};

/** 停留时长（毫秒），对齐后台 toastr 的 timeOut 配置 */
const DURATION = 5000;

let layerPromise = null;

/** 惰性获取 layer 实例（首次调用时等待 layui 模块就绪） */
function getLayer() {
  if (!layerPromise) {
    layerPromise = loadLayui('layer').then(function (m) { return m[0]; });
  }
  return layerPromise;
}

/** 弹出提示：title 可选，以「title · message」合并展示 */
function show(type, message, title) {
  const text = title ? title + ' · ' + message : message;
  getLayer().then(function (layer) {
    layer.msg(text, { icon: ICONS[type] ?? -1, time: DURATION });
  }).catch(function (e) {
    console.error('[toast] layui layer 加载失败（页面需引入 /assets/layui/2.13.9/layui.js）：', e && e.message);
  });
}

export const toast = {
  success: function (message, title) { show('success', message, title); },
  warning: function (message, title) { show('warning', message, title); },
  error: function (message, title) { show('error', message, title); },
  info: function (message, title) { show('info', message, title); }
};
