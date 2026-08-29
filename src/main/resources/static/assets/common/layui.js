/**
 * layui 模块加载器（ES Module，两端通用）—— layui.use 的 Promise 化包装。
 *
 * 页面需已以普通脚本加载 layui.js（全模块一体构建）；
 * 组件内部通过本加载器惰性获取 layer/form/laypage 等模块实例，
 * 调用方以 await/then 使用，无需关心 layui.use 的回调时序。
 *
 * 用法：
 *   const [layer, laypage] = await loadLayui('layer', 'laypage');
 */

/**
 * 加载指定的 layui 模块。
 * @param {...string} names 模块名（如 'layer'、'form'、'laypage'、'flow'）
 * @returns {Promise<Array>} 按传入顺序返回模块实例数组
 */
export function loadLayui(...names) {
  return new Promise(function (resolve, reject) {
    if (!window.layui) {
      reject(new Error('layui 未加载：页面需先以普通脚本引入 /assets/layui/2.13.9/layui.js'));
      return;
    }
    window.layui.use(names, function () {
      resolve(names.map(function (n) { return window.layui[n]; }));
    });
  });
}
