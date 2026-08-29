/**
 * 点点滴滴 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { openCrudDialog, confirmDelete, mockRequest, nextId } from '../crud-dialog.js';
import { toast } from '/assets/common/toast.js';
import { renderAdminTable } from '../datatable-init.js';

/** 文章列表演示数据（原站静态行迁移；后端接口实现后由 table.url 取数） */
const LITTLE_ROWS = [
    { id: 1, title: 'Like_Girl 默认文章语法', date: '2022-11-20', author: 'Ki.' }
];

/**
 * 文章 CRUD API（mock 实现）。
 * TODO(后端): 接口实现后替换为真实请求，例如：
 *   create: (data) => fetch('/admin/api/articles', { method: 'POST', body: JSON.stringify(data) })
 */
const api = {
    create(data) {
        return mockRequest('新增文章成功！', function () {
            LITTLE_ROWS.unshift({
                id: nextId(LITTLE_ROWS), title: data.title, author: data.author,
                date: new Date().toISOString().slice(0, 10)
            });
        });
    },
    update(id, data) {
        return mockRequest('文章修改成功！', function () {
            const row = LITTLE_ROWS.find(function (r) { return String(r.id) === String(id); });
            if (row) Object.assign(row, { title: data.title, author: data.author });
        });
    },
    remove(id) {
        return mockRequest('文章删除成功！', function () {
            const i = LITTLE_ROWS.findIndex(function (r) { return String(r.id) === String(id); });
            if (i > -1) LITTLE_ROWS.splice(i, 1);
        });
    }
};

/** 新增/修改文章表单字段配置 */
const ARTICLE_FIELDS = [
    { name: 'author', label: '发布者', required: true, placeholder: '请输入发布者昵称' },
    { name: 'title', label: '文章标题', required: true, placeholder: '请输入文章标题' }
];

export function init() {
    $(function () {
        // 登录成功欢迎提示（仅从登录页跳转过来时展示一次）
        if (sessionStorage.getItem('ADMIN_LOGIN_WELCOME') === '1') {
            sessionStorage.removeItem('ADMIN_LOGIN_WELCOME');
            toast.success('登录成功！欢迎回来~');
        }
    });

    // 文章列表：layui table 常规页码分页（原站列：序号/标题/发布时间/发布者/操作）
    function renderTable() {
    renderAdminTable({
            elem: '#little-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'title', title: '标题', minWidth: 200 },
            { field: 'date', title: '发布时间', width: 130 },
            { field: 'author', title: '发布者', width: 130 },
            { title: '操作', width: 230, templet: function (d) {
                // 按钮形态与原站一致：修改（btn-secondary）+ 删除（btn-danger）
                return '<a href="javascript:void(0);" class="layui-btn layui-btn-xs js-demo-edit" data-id="' + d.id + '">'
                    + '<i class="layui-icon layui-icon-edit"></i>修改</a> '
                    + '<a class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + d.id + '" data-title="' + esc(d.title) + '">'
                    + '<i class="layui-icon layui-icon-delete"></i>删除</a>';
            } }
        ]],
        data: LITTLE_ROWS
    });
    }

    renderTable();

    // 删除文章：确认提示 + API 层处理（mock），数据变更后重渲染表格
    function removeRow(id, title) {
        confirmDelete({
            message: '您确认要删除标题为 ' + title + ' 的文章吗',
            onDelete: function () {
                return api.remove(id).then(renderTable);
            }
        });
    }

    // 行内按钮（事件委托）：“删除”走确认提示 + API，“修改”弹表单弹框预填行数据。
    // 表格行由 layui 动态渲染，须委托绑定在内容区外壳（pjax 不替换外壳，_bound 防重入叠加）
    const page = document.querySelector('.content-page');
    if (page && !page._rowActionBound) {
        page._rowActionBound = true;
        page.addEventListener('click', function (e) {
            const delEl = e.target.closest('.delete-btn');
            if (delEl) {
                e.preventDefault();
                removeRow(delEl.dataset.id, delEl.dataset.title);
                return;
            }
            // “修改”按钮：弹表单弹框并预填当前行数据
            const editEl = e.target.closest('.js-demo-edit');
            if (editEl) {
                e.preventDefault();
                const row = LITTLE_ROWS.find(function (r) { return String(r.id) === String(editEl.dataset.id); });
                if (!row) return;
                openCrudDialog({
                    title: '修改文章',
                    fields: ARTICLE_FIELDS,
                    initial: row,
                    onSubmit: function (values) {
                        return api.update(row.id, values).then(renderTable);
                    }
                });
            }
        });
    }

    // “新增”按钮：弹出新增文章表单
    $('.js-add-little').on('click', function () {
        openCrudDialog({
            title: '新增文章',
            fields: ARTICLE_FIELDS,
            onSubmit: function (values) {
                return api.create(values).then(renderTable);
            }
        });
    });

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
    // 原站此处会异步加载 wiki.kikiw.cn 的版本信息弹窗（loadModalContent），
    // 属外部站点依赖，已按项目要求整体移除。
}
