/**
 * 后台 DataTable 初始化（ES Module，可重复调用）。
 *
 * 后台列表页（留言管理 / 点点滴滴等）的 #basic-datatable 通过本模块渲染为
 * DataTables 表格（排序 / 分页 / 搜索 / 响应式），依赖页面上以普通脚本加载的
 * jQuery 与 vendor 目录下的全套 DataTables 脚本。
 *
 * 整页加载由 admin/main.js 调用 initAdminDataTables()；
 * 局部刷新注入新内容区后同样经 main.js 调度，可安全重复调用。
 */

/** 后台页面依赖的全局 jQuery（主题与 vendor 脚本以普通脚本形式加载） */
const $ = window.jQuery;

/**
 * 初始化页面内所有 #basic-datatable 表格。
 * 已初始化过的表格自动跳过，可安全重复调用（pjax 局部注入后再次调用）。
 */
export function initAdminDataTables() {
  if (!$.fn.DataTable) {
    return;
  }
  $('#basic-datatable').each(function () {
    if (!$.fn.DataTable.isDataTable(this)) {
      $(this).DataTable({
        responsive: true
      });
    }
  });
}

/**
 * 销毁作用域内已初始化的 DataTable。
 * 局部刷新会丢弃旧表格节点，先销毁以释放 DataTables 挂载在节点上的状态。
 */
export function destroyAdminDataTables(scope) {
  if (!$.fn.DataTable) {
    return;
  }
  scope.find('table').each(function () {
    if ($.fn.DataTable.isDataTable(this)) {
      $(this).DataTable().destroy();
    }
  });
}
