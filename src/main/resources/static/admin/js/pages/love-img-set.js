/**
 * 恋爱相册 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { openCrudDialog, confirmDelete, mockRequest, nextId } from '../crud-dialog.js';
import { toast } from '/assets/common/toast.js';
import { renderAdminTable } from '../datatable-init.js';

/** 恋爱相册列表演示数据（原站静态行迁移；后端接口实现后由 table.url 取数） */
const LOVE_IMG_ROWS = [
    { id: 2, desc: '我们结婚啦', date: '2022-08-15' }
];

/**
 * 恋爱相册 CRUD API（mock 实现）。
 * TODO(后端): 接口实现后替换为真实请求，例如：
 *   create: (data) => fetch('/admin/api/love-images', { method: 'POST', body: JSON.stringify(data) })
 */
const api = {
    create(data) {
        return mockRequest('新增相册成功！', function () {
            LOVE_IMG_ROWS.unshift({ id: nextId(LOVE_IMG_ROWS), desc: data.desc, date: data.date });
        });
    },
    update(id, data) {
        return mockRequest('相册修改成功！', function () {
            const row = LOVE_IMG_ROWS.find(function (r) { return String(r.id) === String(id); });
            if (row) Object.assign(row, { desc: data.desc, date: data.date });
        });
    },
    remove(id) {
        return mockRequest('相册删除成功！', function () {
            const i = LOVE_IMG_ROWS.findIndex(function (r) { return String(r.id) === String(id); });
            if (i > -1) LOVE_IMG_ROWS.splice(i, 1);
        });
    }
};

/** 新增/修改相册表单字段配置 */
const IMG_FIELDS = [
    { name: 'desc', label: '图片描述', required: true, placeholder: '请输入图片描述' },
    { name: 'date', label: '日期', required: true, placeholder: '如 2022-08-15' }
];

export function init() {
    // 恋爱相册列表：layui table 常规页码分页
    function renderTable() {
    return renderAdminTable({
            elem: '#love-img-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'desc', title: '图片描述', minWidth: 200 },
            { field: 'date', title: '日期', width: 180 },
            { title: '操作', width: 220, templet: function (d) {
                return '<a href="javascript:void(0);" class="layui-btn layui-btn-xs js-mock-edit" data-id="' + d.id + '">'
                    + '<i class=" layui-icon layui-icon-edit"></i>修改</a> '
                    + '<a href="javascript:void(0);" class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + d.id + '" data-desc="' + esc(d.desc) + '">'
                    + '<i class=" layui-icon layui-icon-delete"></i>删除</a>';
            } }
        ]],
        data: LOVE_IMG_ROWS
    });
    }

    renderTable();

    // 表格行内按钮（事件委托）：layui table 动态渲染的行必须用委托才能命中。
    // 绑定在内容区外壳（pjax 不替换外壳，_rowBtnBound 防重入叠加）
    const page = document.querySelector('.content-page');
    if (page && !page._rowBtnBound) {
        page._rowBtnBound = true;
        page.addEventListener('click', function (e) {
            // 修改相册：原站跳转编辑页（modImg.php?id=x），现弹表单弹框预填行数据
            const editBtn = e.target.closest('.js-mock-edit');
            if (editBtn) {
                e.preventDefault();
                const row = LOVE_IMG_ROWS.find(function (r) { return String(r.id) === String(editBtn.dataset.id); });
                if (!row) return;
                openCrudDialog({
                    title: '修改相册',
                    fields: IMG_FIELDS,
                    initial: row,
                    onSubmit: function (values) {
                        return api.update(row.id, values).then(renderTable);
                    }
                });
                return;
            }
            // 删除相册：读取 data-id / data-desc 后走确认提示
            const delBtn = e.target.closest('.delete-btn');
            if (delBtn) {
                e.preventDefault();
                removeRow(delBtn.dataset.id, delBtn.dataset.desc);
            }
        });
    }

    // 新增相册入口（原站跳转 loveImgAdd.php 新增页）：弹出新增表单
    $('.js-add-img').on('click', function () {
        openCrudDialog({
            title: '新增相册',
            fields: IMG_FIELDS,
            onSubmit: function (values) {
                return api.create(values).then(renderTable);
            }
        });
    });

    // 删除相册：确认提示 + API 层处理（mock），数据变更后重渲染表格
    function removeRow(id, desc) {
        confirmDelete({
            message: '您确认要删除描述为 ' + desc + ' 的相册吗',
            onDelete: function () {
                return api.remove(id).then(renderTable);
            }
        });
    }

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
}
