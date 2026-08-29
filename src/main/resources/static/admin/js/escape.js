/**
 * HTML 转义（ES Module，两端通用）。
 *
 * 用途：表格 templet / 模板字符串拼接动态值时统一转义，防 XSS 注入；
 * 后端接入真实数据后同样生效。
 */

/**
 * 转义文本中的 HTML 特殊字符（& < > " '）。
 * @param {*} str 任意输入（null/undefined 转为空串）
 * @returns {string} 转义后的安全文本
 */
export function escapeHtml(str) {
  return String(str == null ? '' : str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
