import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
/**
 * 全局设置 页面模块（由 pjax 页面调度加载）。
 */
export function init() {
    // select 下拉改由 layui form 渲染（Webanimation），取值仍走原生 val()
    loadLayui('form').then(function (m) { m[0].render('select'); });

    // 信息配置表单提交（原站通过 $.ajax 提交至 userPost.php，取值逻辑保留）
    $("#userPost").click(function () {
        const Webanimation = $("select[name='Webanimation']").val();
        const userName = $("input[name='userName']").val();
        const userQQ = $("input[name='userQQ']").val();
        const adminName = $("input[name='adminName']").val();
        const pw = $("input[name='pw']").val();
        const SCode = $("input[name='SCode']").val();
        const cssCon = $("textarea[name='cssCon']").val();
        const headCon = $("textarea[name='headCon']").val();
        const footerCon = $("textarea[name='footerCon']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("更新登录信息成功！", "Like_Girl");
        toast.success("更新全局信息成功", "Like_Girl");
        toast.success("更新自定义内容成功", "Like_Girl");
    })

    // 原站此处的 loadModalContent() 会请求 wiki.kikiw.cn 外部数据，属于外部站点依赖，已按规范移除
}
