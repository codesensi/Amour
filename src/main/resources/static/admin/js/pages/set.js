import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
/**
 * 基本设置 页面模块（由 pjax 页面调度加载）。
 */
export function init() {
    // select 下拉改由 layui form 渲染（WebBlur/WebPjax），取值仍走原生 val()
    loadLayui('form').then(function (m) { m[0].render('select'); });

    // 基本设置表单提交（原站通过 $.ajax 提交至 adminPost.php，取值逻辑保留）
    $("#adminPost").click(function () {
        const title = $("input[name='title']").val();
        const logo = $("input[name='logo']").val();
        const writing = $("input[name='writing']").val();
        const WebBlur = $("select[name='WebBlur']").val();
        const WebPjax = $("select[name='WebPjax']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("基本信息修改成功！", "Like_Girl");
        toast.success("开关设置成功！", "Like_Girl");
    })

    // 情侣配置表单提交（原站通过 $.ajax 提交至 loveadminPost.php，取值逻辑保留）
    $("#loveadminPost").click(function () {
        const boy = $("input[name='boy']").val();
        const girl = $("input[name='girl']").val();
        const boyimg = $("input[name='boyimg']").val();
        const girlimg = $("input[name='girlimg']").val();
        const startTime = $("input[name='startTime']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("情侣信息修改成功！", "Like_Girl");
    })

    // 卡片配置&版权配置表单提交（原站通过 $.ajax 提交至 CardadminPost.php，取值逻辑保留）
    $("#CardadminPost").click(function () {
        const bgimg = $("input[name='bgimg']").val();
        const card1 = $("input[name='card1']").val();
        const deci1 = $("input[name='deci1']").val();
        const card2 = $("input[name='card2']").val();
        const deci2 = $("input[name='deci2']").val();
        const card3 = $("input[name='card3']").val();
        const deci3 = $("input[name='deci3']").val();
        const icp = $("input[name='icp']").val();
        const Copyright = $("input[name='Copyright']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("卡片信息修改成功！", "Like_Girl");
    })

    // 原站此处的 loadModalContent() 会请求 wiki.kikiw.cn 外部数据，属于外部站点依赖，已按规范移除
}
