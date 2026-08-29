/*!
 * DataTables 演示初始化脚本
 * 后台列表页（留言管理 / 点点滴滴等）的 #basic-datatable 通过本文件渲染为
 * DataTables 表格（排序 / 分页 / 搜索 / 响应式），依赖 vendor 目录下的全套
 * DataTables 脚本，需在 jQuery 之后、页面交互脚本之前加载。
 */
$(function () {
    // 基础数据表格：与原站一致，开启响应式展开/收起
    $('#basic-datatable').DataTable({
        responsive: true
    });
});
