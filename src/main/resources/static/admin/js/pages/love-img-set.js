/**
 * 恋爱相册 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
import { renderAdminTable } from '../datatable-init.js';

/** 恋爱相册列表演示数据（原站静态行迁移；后端接口实现后由 table.url 取数） */
const LOVE_IMG_ROWS = [
    { id: 2, desc: '我们结婚啦', date: '2022-08-15' }
];

export function init() {
    // 恋爱相册列表：layui table 常规页码分页
    renderAdminTable({
            elem: '#love-img-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'desc', title: '图片描述', minWidth: 200 },
            { field: 'date', title: '日期', width: 180 },
            { title: '操作', width: 220, templet: function (d) {
                return '<a href="javascript:void(0);" class="layui-btn layui-btn-xs js-mock-edit">'
                    + '<i class=" layui-icon layui-icon-edit"></i>修改</a> '
                    + '<a href="javascript:void(0);" class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + d.id + '" data-desc="' + esc(d.desc) + '">'
                    + '<i class=" layui-icon layui-icon-delete"></i>删除</a>';
            } }
        ]],
        data: LOVE_IMG_ROWS
    });

    // 表格行内按钮（事件委托）：layui table 动态渲染的行必须用委托才能命中。
    // 绑定在内容区外壳（pjax 不替换外壳，_rowBtnBound 防重入叠加）
    const page = document.querySelector('.content-page');
    if (page && !page._rowBtnBound) {
        page._rowBtnBound = true;
        page.addEventListener('click', function (e) {
            // 修改相册：原站跳转编辑页（modImg.php?id=x），对应页面暂未迁移，走演示提示
            const editBtn = e.target.closest('.js-mock-edit');
            if (editBtn) {
                e.preventDefault();
                demoTip();
                return;
            }
            // 删除相册：读取 data-id / data-desc 后走 layer.confirm 确认
            const delBtn = e.target.closest('.delete-btn');
            if (delBtn) {
                e.preventDefault();
                removeRow(delBtn.dataset.id, delBtn.dataset.desc);
            }
        });
    }

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
}
