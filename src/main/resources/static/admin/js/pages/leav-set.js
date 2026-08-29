/**
 * 留言管理 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { confirmDelete, mockRequest } from '../crud-dialog.js';
import { toast } from '/assets/common/toast.js';
import { renderAdminTable } from '../datatable-init.js';

/** 留言列表演示数据（原站静态行迁移；后端接口实现后由 table.url 取数） */
const MESSAGES = [
    {
        id: 1,
        content: 'Like Girl 5.2.1-Stable 默认留言',
        date: '2025-09-02 16:24:09',
        name: 'Ki.',
        qq: '3439780232',
        ip: '223.104.79.236',
        region: '广东'
    }
];

export function init() {
    $(function () {
        // 登录成功欢迎提示（仅从登录页跳转过来时展示一次）
        if (sessionStorage.getItem('ADMIN_LOGIN_WELCOME') === '1') {
            sessionStorage.removeItem('ADMIN_LOGIN_WELCOME');
            toast.success('登录成功！欢迎回来~');
        }
    });

    // 留言列表：layui table 常规页码分页
    function renderTable() {
    renderAdminTable({
            elem: '#leav-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'content', title: '留言内容', minWidth: 200, templet: function (d) { return esc(d.content); } },
            { field: 'date', title: 'Date', width: 180 },
            { field: 'name', title: 'Name', width: 130 },
            { field: 'qq', title: 'QQ', width: 130 },
            { field: 'ip', title: 'IP', width: 180, templet: function (d) { return '<span class="layui-badge layui-bg-red">' + esc(d.ip) + '</span> <i>' + esc(d.region) + '</i>'; } },
            { title: 'Action', width: 130, templet: function (d) {
                return '<a class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + esc(d.id) + '" data-content="' + esc(d.content) + '">删除</a>';
            } }
        ]],
        data: MESSAGES
    });
    }

    renderTable();

    // 删除留言按钮（事件委托）：确认提示 + API 层处理（mock），数据变更后重渲染表格。
    // 绑定在内容区外壳（pjax 不替换外壳，_bound 防重入叠加）
    const page = document.querySelector('.content-page');
    if (page && !page._deleteBtnBound) {
        page._deleteBtnBound = true;
        page.addEventListener('click', function (e) {
            const el = e.target.closest('.delete-btn');
            if (!el) return;
            e.preventDefault();
            removeRow(el.dataset.id, el.dataset.content);
        });
    }

    // 删除留言：确认提示 + API 层处理（mock）
    // TODO(后端): 接口实现后 api.remove 替换为 fetch('/admin/api/messages/' + id, { method: 'DELETE' })
    function removeRow(id, text) {
        confirmDelete({
            message: '您确认要删除 ' + text + ' 内容吗',
            onDelete: function () {
                return mockRequest('删除留言成功！', function () {
                    const i = MESSAGES.findIndex(function (r) { return String(r.id) === String(id); });
                    if (i > -1) MESSAGES.splice(i, 1);
                }).then(renderTable);
            }
        });
    }

    // “留言相关设置”按钮：原站跳转 /admin/leavP.php 留言设置页（暂未迁移），演示环境仅提示
    $('.js-leav-config').on('click', function () {
        toast.info("演示数据：留言设置页面暂未迁移", "Like_Girl");
    });

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
    // 原站此处会异步加载 wiki.kikiw.cn 的版本信息弹窗（loadModalContent），
    // 属外部站点依赖，已按项目要求整体移除。
}
