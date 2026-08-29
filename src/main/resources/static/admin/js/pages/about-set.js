/**
 * 关于页面 页面模块（由 pjax 页面调度加载）。
 */
import { bindMockSubmissions } from '../mock-submit.js';
import { escapeHtml as esc } from '../escape.js';
import { toast } from '/assets/common/toast.js';

export function init() {
    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
}
