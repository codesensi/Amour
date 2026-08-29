/**
 * IP/拉黑 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
import { renderAdminTable } from '../datatable-init.js';

/** IP 封禁列表演示数据（原站由后端循环输出；后端接口实现后由 table.url 取数） */
const IP_BANS = [
    { id: 1, region: '广东', date: '2025-09-02 16:24:09', remark: '恶意扫描后台', ip: '223.104.79.236' },
    { id: 2, region: '香港', date: '2025-08-30 09:02:41', remark: '暴力破解登录', ip: '45.194.8.11' }
];

export function init() {
    // IP 封禁列表：layui table 常规页码分页（原站列：序号/IP归属地/Date/备注/IP/Action）
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

    // 解封按钮（事件委托）：layer.confirm 确认后给出演示提示，不做真实解封。
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

    // 解封 IP：原站跳转 delip.php?id=x 执行删除，现阶段后端接口未实现，改为演示提示
    function removeRow(id, ip) {
        loadLayui('layer').then(function (m) {
            m[0].confirm('您确认要删除IP为 ' + ip + ' 吗', { title: '解封确认' }, function (index) {
                m[0].close(index);
                // 现阶段后端接口未实现，mock 演示提示；接口实现后恢复为真实请求（delip.php）
                toast.warning("演示数据：解封功能暂未接入后端", "Like_Girl");
            });
        });
    }

    // 新增 IP 封禁入口：原站跳转 ipSet.php（新增页），现阶段该页面未实现，改为演示提示
    $('.js-add-ip').on("click", function () {
        // 现阶段后端接口未实现，mock 演示提示；接口实现后恢复为真实跳转（ipSet.php）
        toast.info("演示数据：新增 IP 封禁页面暂未开放！", "Like_Girl");
    });

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
    $(function () {
        // 原站此处会请求 wiki.kikiw.cn 拉取版本公告弹窗数据（loadModalContent），属外部站点依赖，已移除
    })
}
