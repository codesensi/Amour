/**
 * 后台列表表格（ES Module）—— layui table 封装。
 *
 * 各列表页的表格统一经 renderAdminTable 渲染为 layui table（常规页码分页/
 * 排序/响应式），替换原 jQuery DataTables 方案后，views 页面不再需要
 * vendor 目录的全套 DataTables 脚本。
 *
 * 使用方式：页面模块在 init() 中传入各自的表格元素与列配置：
 *   import { renderAdminTable } from '../datatable-init.js';
 *   renderAdminTable({ elem: '#<页面>-table', cols: [[...]], data: MOCK_ROWS });
 */

import { loadLayui } from '/assets/common/layui.js';

/** 每页条数（常规页码分页，默认 20 条/页） */
export const TABLE_PAGE_SIZE = 20;

/**
 * 渲染后台列表表格。
 * @param {object} config layui table 配置（elem、cols 必填；data/url 选填）
 * @returns {Promise<void>}
 */
export async function renderAdminTable(config) {
  const table = (await loadLayui('table'))[0];
  table.render(Object.assign({
    page: { limit: TABLE_PAGE_SIZE, limits: [10, 20, 30, 50] }
  }, config));
}
