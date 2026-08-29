/**
 * DataTables 初始化入口（可重复调用）。
 *
 * 后台列表页（留言管理 / 点点点点滴滴等）的 #basic-datatable 通过本文件
 * 渲染为 DataTables 表格（排序 / 分页 / 搜索 / 响应式），依赖 vendor 目录下
 * 的全套 DataTables 脚本，需在 jQuery 之后、页面交互脚本之前加载。
 *
 * 局部刷新（admin-pjax.js）注入新内容区后也会调用此入口，
 * 因此初始化逻辑封装为全局函数并内置防重复初始化判断。
 */

/**
 * 初始化页面内所有 #basic-datatable 表格。
 * 已初始化过的表格自动跳过，可安全重复调用。
 */
function initAdminDataTables() {
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

// 整页加载时初始化一次；局部刷新场景由 admin-pjax.js 注入后调用
$(function () {
    initAdminDataTables();
});
