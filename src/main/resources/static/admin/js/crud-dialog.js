/**
 * CRUD 弹框（ES Module）—— 管理端"新增/修改表单弹框 + 删除确认"统一交互实现。
 *
 * 行业规范约定：
 * - 表单弹框：layer.open(type:1) 承载 layui form，确认按钮经 lay-submit 拦截，
 *   由 layui 内置校验（lay-verify）保证必填项；
 * - 确认回调 onSubmit/onDelete 须返回 Promise：resolve = 操作成功（关闭弹框），
 *   reject = 操作失败（弹框保留并提示错误）；
 * - Promise 由调用方页面的 API 层提供。现阶段为 mock 实现（本地变更演示数据 +
 *   模拟网络延迟），后端接口实现后仅需把 API 层替换为真实 fetch 请求，
 *   本组件与页面接线无需变动。
 */
import { loadLayui } from '/assets/common/layui.js';
import { escapeHtml as esc } from './escape.js';
import { toast } from '/assets/common/toast.js';

/** 表单提交回调的当前引用（form.on 对同一 filter 全局注册一次，换弹框只换引用） */
let currentSubmit = null;
let formHooked = false;

/**
 * 打开新增/修改表单弹框。
 *
 * @param {object} options
 * @param {string} options.title 弹框标题（"新增事件"/"修改相册"等）
 * @param {Array<{name: string, label: string, type?: 'text'|'select'|'textarea',
 *         options?: Array<{value: string, label: string}>, required?: boolean,
 *         placeholder?: string, value?: string}>} options.fields 字段配置
 * @param {object} [options.initial] 修改场景的初始行数据（按 name 取值预填）
 * @param {(values: object) => Promise<void>} options.onSubmit 确认回调（API 层返回 Promise）
 * @returns {Promise<void>}
 */
export async function openCrudDialog({ title, fields, initial = {}, onSubmit }) {
  const layer = (await loadLayui('layer'))[0];
  const form = (await loadLayui('form'))[0];

  const body = fields.map(function (f) {
    const value = initial[f.name] != null ? initial[f.name] : (f.value || '');
    const required = f.required ? ' lay-verify="required"' : '';
    const placeholder = f.placeholder || '';
    let control;
    if (f.type === 'select') {
      const opts = f.options.map(function (o) {
        return '<option value="' + esc(o.value) + '"' + (String(value) === String(o.value) ? ' selected' : '') + '>'
            + esc(o.label) + '</option>';
      }).join('');
      control = '<select name="' + esc(f.name) + '"' + required + '>' + opts + '</select>';
    } else if (f.type === 'textarea') {
      control = '<textarea name="' + esc(f.name) + '" class="layui-textarea" placeholder="'
          + esc(placeholder) + '"' + required + '>' + esc(value) + '</textarea>';
    } else {
      control = '<input type="text" name="' + esc(f.name) + '" class="layui-input" value="'
          + esc(value) + '" placeholder="' + esc(placeholder) + '"' + required + '>';
    }
    return '<div class="layui-form-item"><label class="layui-form-label">' + esc(f.label)
        + '</label><div class="layui-input-block">' + control + '</div></div>';
  }).join('');

  const html = '<form class="layui-form crud-dialog-form" style="padding: 24px 24px 12px;">'
      + body
      + '<div class="layui-form-item" style="margin-bottom: 0;">'
      + '<button type="button" class="layui-btn layui-btn-primary js-crud-cancel" style="width: 48%; margin-right: 4%;">取消</button>'
      + '<button class="layui-btn" style="width: 48%;" lay-submit lay-filter="crud-dialog-submit">确认</button>'
      + '</div></form>';

  const layerIndex = layer.open({ type: 1, title: title, area: '480px', content: html });
  form.render(null, 'crud-dialog-form');

  // 取消按钮：仅关闭弹框，不触发 API 请求（弹框 DOM 由 layer 关闭时移除，无叠加问题）
  const cancelBtn = document.querySelector('.crud-dialog-form .js-crud-cancel');
  if (cancelBtn) {
    cancelBtn.addEventListener('click', function () { layer.close(layerIndex); });
  }

  // layui form 对同一 filter 的 submit 注册是叠加的，这里仅注册一次，
  // 后续弹框通过替换 currentSubmit 引用切换回调
  if (!formHooked) {
    formHooked = true;
    form.on('submit(crud-dialog-submit)', function (data) {
      if (currentSubmit) currentSubmit(data.field);
      return false; // 阻止表单默认提交与页面跳转
    });
  }
  currentSubmit = function (values) {
    Promise.resolve(onSubmit(values))
      .then(function () { layer.close(layerIndex); })
      .catch(function (msg) { toast.error(msg || '操作失败，请重试'); });
  };
}

/**
 * 删除确认框。
 *
 * @param {object} options
 * @param {string} options.message 确认文案（含待删对象的标识信息）
 * @param {string} [options.title] 确认框标题，默认"删除确认"
 * @param {() => Promise<void>} options.onDelete 确认后的删除回调（API 层返回 Promise）
 */
export function confirmDelete({ message, title = '删除确认', onDelete }) {
  return loadLayui('layer').then(function (m) {
    m[0].confirm(message, { title: title }, function (index) {
      m[0].close(index);
      Promise.resolve(onDelete());
    });
  });
}

/**
 * 计算下一可用 id（取当前集合最大 id + 1）。
 *
 * @param {Array<{id: number}>} rows 演示数据集合
 * @returns {number}
 */
export function nextId(rows) {
    return rows.reduce(function (max, r) { return Math.max(max, Number(r.id) || 0); }, 0) + 1;
}

/**
 * mock 请求：模拟约 300ms 网络延迟后执行本地数据变更并提示成功。
 *
 * TODO(后端): 各页面的 create/update/remove 接口实现后，
 * 将本函数调用替换为真实请求（fetch 保持返回 Promise 的形态即可）。
 *
 * @param {string} successMsg 成功提示文案
 * @param {() => void} [mutate] 本地演示数据的变更逻辑
 * @returns {Promise<void>}
 */
export function mockRequest(successMsg, mutate) {
  return new Promise(function (resolve) {
    setTimeout(function () {
      if (mutate) mutate();
      toast.success(successMsg);
      resolve();
    }, 300);
  });
}
