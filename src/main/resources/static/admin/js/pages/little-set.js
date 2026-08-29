/**
 * 点点滴滴 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
import { renderAdminTable } from '../datatable-init.js';

/** 文章列表演示数据（原站静态行迁移；后端接口实现后由 table.url 取数） */
const LITTLE_ROWS = [
    { id: 1, title: 'Like_Girl 默认文章语法', date: '2022-11-20', author: 'Ki.' }
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
    renderAdminTable({
            elem: '#little-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'title', title: '标题', minWidth: 200 },
            { field: 'date', title: '发布时间', width: 130 },
            { field: 'author', title: '发布者', width: 130 },
            { title: '操作', width: 230, templet: function (d) {
                // 按钮形态与原站一致：修改（btn-secondary）+ 删除（btn-danger）
                return '<a href="javascript:void(0);" class="layui-btn layui-btn-xs js-demo-edit">'
                    + '<i class="layui-icon layui-icon-edit"></i>修改</a> '
                    + '<a class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + d.id + '" data-title="' + esc(d.title) + '">'
                    + '<i class="layui-icon layui-icon-delete"></i>删除</a>';
            } }
        ]],
        data: LITTLE_ROWS
    });

    // 删除文章：原站确认后跳转 dellitt.php?id=x 执行删除；现为演示数据，仅给出提示
    function removeRow(id, title) {
        loadLayui('layer').then(function (m) {
            m[0].confirm('您确认要删除标题为 ' + title + ' 的文章吗', { title: '删除确认' }, function (index) {
                m[0].close(index);
                // 现阶段后端接口未实现，mock 演示提示；接口实现后恢复为真实删除请求
                toast.warning("演示数据：删除功能暂未接入后端", "Like_Girl");
            });
        });
    }

    // 行内按钮（事件委托）：“删除”确认后给出演示提示，“修改”提示编辑页未迁移。
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
            // “修改”按钮：原站跳转 modlitt.php?id=x 文章编辑页（暂未迁移），演示环境仅提示
            const editEl = e.target.closest('.js-demo-edit');
            if (editEl) {
                e.preventDefault();
                toast.info("演示数据：文章编辑页面暂未迁移", "Like_Girl");
            }
        });
    }

    // “新增”按钮：原站跳转 /admin/littleAdd.php 新增文章页（暂未迁移），演示环境仅提示
    $('.js-add-little').on('click', function () {
        toast.info("演示数据：新增文章页面暂未迁移", "Like_Girl");
    });

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
    // 原站此处会异步加载 wiki.kikiw.cn 的版本信息弹窗（loadModalContent），
    // 属外部站点依赖，已按项目要求整体移除。
}
