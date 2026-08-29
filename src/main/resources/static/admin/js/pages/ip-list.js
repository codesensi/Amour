/**
 * IP/拉黑 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { openCrudDialog, confirmDelete, mockRequest, nextId } from '../crud-dialog.js';
import { toast } from '/assets/common/toast.js';
import { renderAdminTable } from '../datatable-init.js';

/** IP 封禁列表演示数据（原站由后端循环输出；后端接口实现后由 table.url 取数） */
const IP_BANS = [
    { id: 1, region: '广东', date: '2025-09-02 16:24:09', remark: '恶意扫描后台', ip: '223.104.79.236' },
    { id: 2, region: '香港', date: '2025-08-30 09:02:41', remark: '暴力破解登录', ip: '45.194.8.11' }
];

/**
 * IP 封禁 CRUD API（mock 实现；解封即删除封禁记录）。
 * TODO(后端): 接口实现后替换为真实请求，例如：
 *   create: (data) => fetch('/admin/api/ip-bans', { method: 'POST', body: JSON.stringify(data) })
 */
const api = {
    create(data) {
        return mockRequest('IP封禁成功！', function () {
            IP_BANS.unshift({
                id: nextId(IP_BANS), ip: data.ip, remark: data.remark || '',
                region: '未知', date: new Date().toLocaleString('zh-CN', { hour12: false })
            });
        });
    },
    remove(id) {
        return mockRequest('解封成功！', function () {
            const i = IP_BANS.findIndex(function (r) { return String(r.id) === String(id); });
            if (i > -1) IP_BANS.splice(i, 1);
        });
    }
};

/** 新增 IP 封禁表单字段配置 */
const IP_FIELDS = [
    { name: 'ip', label: 'IP 地址', required: true, placeholder: '请输入需要封禁的 IP' },
    { name: 'remark', label: '备注', placeholder: '选填，封禁原因' }
];

export function init() {
    // IP 封禁列表：layui table 常规页码分页（原站列：序号/IP归属地/Date/备注/IP/Action）
    function renderTable() {
    renderAdminTable({
            elem: '#ip-ban-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'region', title: 'IP归属地', width: 130 },
            { field: 'date', title: 'Date', width: 180 },
            { field: 'remark', title: '备注', minWidth: 160 },
            { field: 'ip', title: 'IP', width: 180 },
            { title: 'Action', width: 125, templet: function (d) {
                return '<a class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + d.id + '" data-ip="' + esc(d.ip) + '">解封</a>';
            } }
        ]],
        data: IP_BANS
    });
    }

    renderTable();

    // 解封按钮（事件委托）：确认提示 + API 层处理（mock），数据变更后重渲染表格。
    // 绑定在内容区外壳（pjax 不替换外壳，_bound 防重入叠加）
    const page = document.querySelector('.content-page');
    if (page && !page._deleteBtnBound) {
        page._deleteBtnBound = true;
        page.addEventListener('click', function (e) {
            const el = e.target.closest('.delete-btn');
            if (!el) return;
            e.preventDefault();
            removeRow(el.dataset.id, el.dataset.ip);
        });
    }

    // 解封 IP：确认提示 + API 层处理（mock）
    function removeRow(id, ip) {
        confirmDelete({
            message: '您确认要解封IP为 ' + ip + ' 的记录吗',
            title: '解封确认',
            onDelete: function () {
                return api.remove(id).then(renderTable);
            }
        });
    }

    // 新增 IP 封禁入口（原站跳转 ipSet.php 新增页）：弹出新增表单
    $('.js-add-ip').on('click', function () {
        openCrudDialog({
            title: '新增 IP 封禁',
            fields: IP_FIELDS,
            onSubmit: function (values) {
                return api.create(values).then(renderTable);
            }
        });
    });

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
}
