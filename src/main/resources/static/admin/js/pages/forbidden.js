/**
 * 非法访问 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
import { renderAdminTable } from '../datatable-init.js';

/** 非法访问记录演示数据（原站由后端循环输出；后端接口实现后由 table.url 取数） */
const FORBIDDEN_ROWS = [
    { time: '2025-09-02 16:24:09', path: '/admin/views/config.html', ip: '223.104.79.236', region: '广东' },
    { time: '2025-08-30 09:11:47', path: '/uploads/../etc/passwd', ip: '45.194.8.11', region: '香港' }
];

export function init() {
    // 非法访问记录列表：layui table 常规页码分页（原站列：序号/访问时间/非法文件路径/IP地址/IP归属地）
    renderAdminTable({
            elem: '#forbidden-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'time', title: '访问时间', width: 180 },
            { field: 'path', title: '非法文件路径', minWidth: 200 },
            { field: 'ip', title: 'IP地址', width: 180 },
            { field: 'region', title: 'IP归属地', width: 130 }
        ]],
        data: FORBIDDEN_ROWS
    });

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
    $(function () {
        // 原站此处会请求 wiki.kikiw.cn 拉取版本公告弹窗数据（loadModalContent），属外部站点依赖，已移除
    })
}
