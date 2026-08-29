/**
 * 后台列表表格（ES Module）—— layui table 封装。
 *
 * 后台列表页的 #basic-datatable 统一经 renderAdminTable 渲染为 layui table
 * （常规页码分页/排序/响应式），替换原 jQuery DataTables 方案后，
 * views 页面不再需要 vendor 目录的全套 DataTables 脚本。
 *
 * 使用方式：页面模块在 init() 中传入各自的列配置与数据：
 *   import { renderAdminTable } from '../datatable-init.js';
 *   renderAdminTable({ cols: [[...]], data: MOCK_ROWS });
 */

import { loadLayui } from '/assets/common/layui.js';

/** 每页条数（常规页码分页，默认 20 条/页） */
export const TABLE_PAGE_SIZE = 20;

/**
 * 渲染后台列表表格。
 * @param {object} config layui table 配置（cols 必填；data/url 选填）
 * @returns {Promise<void>}
 */
export async function renderAdminTable(config) {
  const table = (await loadLayui('table'))[0];
  table.render(Object.assign({
    elem: '#basic-datatable',
    page: { limit: TABLE_PAGE_SIZE, limits: [10, 20, 30, 50] }
  }, config));
}

/**
 * 兼容外壳装配调用：列表表格已改由各页面模块经 renderAdminTable 渲染，
 * 此处保留空实现以维持 main.js 的装配流程不变。
 */
export function initAdminDataTables() {}

/**
 * 兼容外壳装配调用：pjax 换页丢弃旧节点即释放表格状态，无需显式销毁。
 */
export function destroyAdminDataTables() {}
